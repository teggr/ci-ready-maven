(() => {
  "use strict";
  const widget = document.getElementById("catalogue-assistant");
  if (!widget || !window.htmx) return;
  const config = window.catalogueAssistant || {};
  let endpoint;
  try {
    endpoint = new URL(config.endpoint);
    const local = ["localhost", "127.0.0.1"].includes(location.hostname);
    if (endpoint.protocol !== "https:" && !(local && endpoint.protocol === "http:" &&
        ["localhost", "127.0.0.1"].includes(endpoint.hostname))) return;
    if (endpoint.username || endpoint.password || endpoint.search || endpoint.hash ||
        endpoint.pathname !== "/assistant/chat") return;
  } catch {
    return;
  }
  const form = document.getElementById("assistant-form");
  const question = document.getElementById("assistant-question");
  const transcript = document.getElementById("assistant-transcript");
  const status = document.getElementById("assistant-status");
  const historyLimit = Math.max(1, Math.min(20, Number(config.maxHistoryTurns) || 4));
  question.maxLength = Math.max(1, Math.min(1000, Number(config.maxQuestionLength) || 1000));
  // Cross-origin requests are required, but response scripts and eval are not.
  htmx.config.selfRequestsOnly = false;
  htmx.config.allowEval = false;
  htmx.config.allowScriptTags = false;
  htmx.config.historyCacheSize = 0;
  form.setAttribute("hx-request", JSON.stringify({
    timeout: Math.max(1000, Math.min(65000, Number(config.requestTimeoutMillis) || 25000))
  }));
  form.setAttribute("hx-post", endpoint.href);
  form.action = endpoint.href;
  htmx.process(form);
  widget.querySelectorAll("button, textarea").forEach(element => { element.disabled = false; });
  document.getElementById("assistant-availability").textContent =
    "Ready. Responses quote documented evidence and link to the source pages.";
  widget.querySelectorAll("[data-question]").forEach(button => {
    button.addEventListener("click", () => {
      question.value = button.dataset.question;
      question.focus();
    });
  });
  form.addEventListener("htmx:configRequest", event => {
    // Do not disclose the current page URL, transcript or arbitrary form values.
    const value = question.value.trim();
    if (!value || value.length > question.maxLength) {
      event.preventDefault();
      status.textContent = "Please enter a shorter catalogue question.";
      return;
    }
    event.detail.parameters = { question: value };
    delete event.detail.headers["HX-Current-URL"];
  });
  form.addEventListener("htmx:beforeRequest", () => {
    form.setAttribute("aria-busy", "true");
    status.textContent = "Looking up catalogue evidence…";
  });
  form.addEventListener("htmx:afterRequest", event => {
    form.removeAttribute("aria-busy");
    if (!event.detail.successful) {
      status.textContent = event.detail.xhr?.status === 429
        ? "Too many requests. Please wait a minute, then retry your question."
        : "The assistant is unavailable. Your question is preserved; please retry or browse the catalogue.";
    }
  });
  transcript.addEventListener("htmx:afterSwap", () => {
    while (transcript.children.length > historyLimit) transcript.firstElementChild.remove();
    transcript.scrollTop = transcript.scrollHeight;
    status.textContent = "Response received. You can ask another question or retry.";
    // Keep the submitted question so that recoverable server errors can be retried.
  });
})();
