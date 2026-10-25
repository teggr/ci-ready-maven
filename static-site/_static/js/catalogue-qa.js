(() => {
  "use strict";

  const component = document.getElementById("catalogue-qa");
  if (!component) return;

  const form = document.getElementById("catalogue-qa-form");
  const question = document.getElementById("catalogue-qa-question");
  const submit = document.getElementById("catalogue-qa-submit");
  const status = document.getElementById("catalogue-qa-status");
  const error = document.getElementById("catalogue-qa-error");
  const result = document.getElementById("catalogue-qa-result");
  const answer = document.getElementById("catalogue-qa-answer");
  const sources = document.getElementById("catalogue-qa-sources");
  const sourceList = document.getElementById("catalogue-qa-source-list");
  const reset = document.getElementById("catalogue-qa-reset");
  const suggestions = [...component.querySelectorAll("[data-catalogue-question]")];

  const CATALOGUE_ORIGIN = "https://teggr.github.io";
  const CATALOGUE_PATH = "/ci-ready-maven/plugins/";
  const REQUEST_TIMEOUT_MS = 35000;
  let inFlight = false;

  const enabled = Boolean(component.dataset.apiBaseUrl.trim());
  question.disabled = !enabled;
  submit.disabled = !enabled;
  suggestions.forEach((button) => {
    button.disabled = !enabled;
  });
  status.textContent = enabled
    ? ""
    : "Catalogue questions are not available yet. You can still browse the catalogue below.";

  form.addEventListener("submit", (event) => {
    event.preventDefault();
    submitQuestion();
  });

  suggestions.forEach((button) => {
    button.addEventListener("click", () => {
      question.value = button.dataset.catalogueQuestion;
      question.focus();
    });
  });

  reset.addEventListener("click", () => {
    question.value = "";
    result.hidden = true;
    error.hidden = true;
    status.textContent = "";
    question.focus();
  });

  async function submitQuestion() {
    if (!enabled || inFlight) return;

    const text = question.value.trim();
    if (!text) {
      showError("Enter a question about the catalogue first.");
      question.focus();
      return;
    }

    inFlight = true;
    setBusy(true);
    error.hidden = true;
    result.hidden = true;
    status.textContent = "Looking through the catalogue…";

    const controller = new AbortController();
    const timeout = window.setTimeout(() => controller.abort(), REQUEST_TIMEOUT_MS);

    try {
      const endpoint = getEndpoint();
      const response = await fetch(endpoint, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ question: text }),
        signal: controller.signal,
      });

      if (!response.ok) {
        const problem = await readProblem(response);
        throw new RequestError(errorMessage(response.status, problem));
      }

      let payload;
      try {
        payload = await response.json();
      } catch (jsonError) {
        if (jsonError && jsonError.name === "AbortError") throw jsonError;
        throw new RequestError("The catalogue returned an unreadable response. Please try again.");
      }

      renderResponse(payload);
      status.textContent = payload.status === "INSUFFICIENT_EVIDENCE"
        ? "The catalogue could not support a specific answer."
        : "Answer ready.";
      result.hidden = false;
    } catch (requestError) {
      if (requestError instanceof RequestError) {
        showError(requestError.message);
      } else if (requestError && requestError.name === "AbortError") {
        showError("The request took too long. Please try again in a moment.");
      } else {
        showError("We couldn't reach the catalogue service. Please try again later.");
      }
    } finally {
      window.clearTimeout(timeout);
      inFlight = false;
      setBusy(false);
    }
  }

  function getEndpoint() {
    const configuredBase = component.dataset.apiBaseUrl.trim();
    if (!configuredBase) {
      throw new RequestError("Catalogue questions aren't connected yet. Please try again later.");
    }

    let base;
    try {
      base = new URL(configuredBase);
    } catch {
      throw new RequestError("The catalogue service is not configured correctly.");
    }

    const isLocalHttp = base.protocol === "http:"
      && ["localhost", "127.0.0.1", "[::1]"].includes(base.hostname);
    if ((!isLocalHttp && base.protocol !== "https:")
      || base.username
      || base.password
      || base.search
      || base.hash) {
      throw new RequestError("The catalogue service is not configured correctly.");
    }

    return new URL("/api/v1/chat", base.origin).href;
  }

  async function readProblem(response) {
    try {
      const body = await response.json();
      return body && typeof body === "object" ? body : {};
    } catch (parseError) {
      if (parseError && parseError.name === "AbortError") throw parseError;
      return {};
    }
  }

  function errorMessage(httpStatus, problem) {
    const safeDetail = typeof problem.detail === "string"
      && problem.detail.length <= 180
      && !/(exception|stack trace| at [\w.$]+\(|provider|internal server)/i.test(problem.detail)
      ? problem.detail.trim()
      : "";

    switch (httpStatus) {
      case 400:
        return safeDetail
          ? `The question could not be accepted: ${safeDetail}`
          : "The question could not be accepted. Try rephrasing it and submit again.";
      case 413:
        return "That question is too long. Please shorten it and try again.";
      case 429:
        return "There are too many questions right now. Please wait a little before trying again.";
      case 502:
        return "The assistant couldn't produce a valid answer. Please try again shortly.";
      case 503:
        return "The catalogue service is temporarily unavailable. Please try again later.";
      case 504:
        return "The catalogue service took too long to respond. Please try again.";
      default:
        return "The catalogue service couldn't answer right now. Please try again later.";
    }
  }

  function renderResponse(payload) {
    if (!payload
      || typeof payload !== "object"
      || !["ANSWERED", "INSUFFICIENT_EVIDENCE"].includes(payload.status)
      || typeof payload.answer !== "string"
      || !payload.answer.trim()
      || !Array.isArray(payload.sources)) {
      throw new RequestError("The catalogue returned an unexpected response. Please try again.");
    }

    answer.replaceChildren();
    renderMarkdown(payload.answer, answer);
    sourceList.replaceChildren();

    const validSources = payload.status === "ANSWERED"
      ? payload.sources.map(validSource).filter(Boolean)
      : [];

    validSources.forEach((source) => {
      const item = document.createElement("li");
      const link = document.createElement("a");
      link.href = source.url;
      link.textContent = source.title;
      item.append(link);
      sourceList.append(item);
    });

    sources.hidden = validSources.length === 0;
  }

  function validSource(source) {
    if (!source || typeof source !== "object"
      || typeof source.id !== "string"
      || !/^[a-z0-9]+(?:-[a-z0-9]+)*$/.test(source.id)
      || typeof source.title !== "string"
      || !source.title.trim()
      || typeof source.catalogueUrl !== "string") {
      return null;
    }

    const expectedUrl = `${CATALOGUE_ORIGIN}${CATALOGUE_PATH}${source.id}.html`;
    try {
      const candidate = new URL(source.catalogueUrl);
      if (candidate.href !== expectedUrl) return null;
    } catch {
      return null;
    }

    return { title: source.title.trim(), url: expectedUrl };
  }

  function renderMarkdown(markdown, container) {
    const lines = markdown.replace(/\r\n?/g, "\n").split("\n");
    let paragraph = [];
    let list = null;
    let codeLines = null;

    const flushParagraph = () => {
      if (!paragraph.length) return;
      const node = document.createElement("p");
      appendInlineMarkdown(node, paragraph.join(" "));
      container.append(node);
      paragraph = [];
    };

    const flushList = () => {
      if (!list) return;
      container.append(list);
      list = null;
    };

    for (const line of lines) {
      if (codeLines) {
        if (/^\s*```/.test(line)) {
          const pre = document.createElement("pre");
          const code = document.createElement("code");
          code.textContent = codeLines.join("\n");
          pre.append(code);
          container.append(pre);
          codeLines = null;
        } else {
          codeLines.push(line);
        }
        continue;
      }

      if (/^\s*```/.test(line)) {
        flushParagraph();
        flushList();
        codeLines = [];
        continue;
      }

      const heading = /^(#{1,3})\s+(.+)$/.exec(line);
      if (heading) {
        flushParagraph();
        flushList();
        const node = document.createElement(`h${Number(heading[1].length) + 1}`);
        appendInlineMarkdown(node, heading[2]);
        container.append(node);
        continue;
      }

      const listItem = /^\s*(?:([-*+])|(\d+)[.)])\s+(.+)$/.exec(line);
      if (listItem) {
        flushParagraph();
        const isOrdered = Boolean(listItem[2]);
        const tag = isOrdered ? "ol" : "ul";
        if (list && list.tagName.toLowerCase() !== tag) flushList();
        if (!list) list = document.createElement(tag);
        const item = document.createElement("li");
        appendInlineMarkdown(item, listItem[3]);
        list.append(item);
        continue;
      }

      if (!line.trim()) {
        flushParagraph();
        flushList();
        continue;
      }

      flushList();
      paragraph.push(line.trim());
    }

    if (codeLines) {
      const pre = document.createElement("pre");
      const code = document.createElement("code");
      code.textContent = codeLines.join("\n");
      pre.append(code);
      container.append(pre);
    }
    flushParagraph();
    flushList();
  }

  function appendInlineMarkdown(parent, text) {
    const tokenPattern = /(`[^`\n]+`|\[[^\]\n]+\]\(https:\/\/[^\s)]+\)|\*\*[^*\n]+\*\*|\*[^*\n]+\*)/g;
    let lastIndex = 0;

    for (const match of text.matchAll(tokenPattern)) {
      if (match.index > lastIndex) {
        parent.append(document.createTextNode(text.slice(lastIndex, match.index)));
      }

      const token = match[0];
      if (token.startsWith("`")) {
        const code = document.createElement("code");
        code.textContent = token.slice(1, -1);
        parent.append(code);
      } else if (token.startsWith("[")) {
        const linkMatch = /^\[([^\]]+)\]\((https:\/\/[^\s)]+)\)$/.exec(token);
        const safeUrl = linkMatch && safeMarkdownUrl(linkMatch[2]);
        if (safeUrl) {
          const link = document.createElement("a");
          link.href = safeUrl;
          link.textContent = linkMatch[1];
          link.rel = "noopener noreferrer";
          parent.append(link);
        } else {
          parent.append(document.createTextNode(token));
        }
      } else if (token.startsWith("**")) {
        const strong = document.createElement("strong");
        strong.textContent = token.slice(2, -2);
        parent.append(strong);
      } else {
        const emphasis = document.createElement("em");
        emphasis.textContent = token.slice(1, -1);
        parent.append(emphasis);
      }

      lastIndex = match.index + token.length;
    }

    if (lastIndex < text.length) {
      parent.append(document.createTextNode(text.slice(lastIndex)));
    }
  }

  function safeMarkdownUrl(value) {
    try {
      const url = new URL(value);
      return url.protocol === "https:" ? url.href : null;
    } catch {
      return null;
    }
  }

  function setBusy(busy) {
    question.disabled = busy;
    submit.disabled = busy;
    submit.textContent = busy ? "Asking…" : "Ask the catalogue";
    suggestions.forEach((button) => {
      button.disabled = busy;
    });
    component.dataset.loading = String(busy);
  }

  function showError(message) {
    error.textContent = message;
    error.hidden = false;
    status.textContent = "The question could not be completed.";
  }

  class RequestError extends Error {}
})();
