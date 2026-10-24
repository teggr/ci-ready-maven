const { test, expect } = require("@playwright/test");
const path = require("node:path");

test.beforeEach(async ({ page }) => {
  await page.route("https://cdn.jsdelivr.net/npm/htmx.org@2.0.8/dist/htmx.min.js", route =>
    route.fulfill({ path: path.join(__dirname, "node_modules/htmx.org/dist/htmx.min.js"),
      contentType: "application/javascript" }));
});

async function enabled(page) {
  await page.route("**/js/assistant-config.js", route => route.fulfill({
    contentType: "application/javascript",
    body: 'window.catalogueAssistant = { endpoint: "https://assistant.example/assistant/chat", maxHistoryTurns: 2 };'
  }));
  await page.goto("/");
}

async function response(route, body, status = 200) {
  await route.fulfill({
    status, contentType: "text/html",
    headers: { "Access-Control-Allow-Origin": "http://localhost:8080" },
    body
  });
}

test("disabled feature preserves catalogue navigation", async ({ page }) => {
  await page.goto("/");
  await expect(page.getByRole("button", { name: "Ask the catalogue" })).toBeDisabled();
  await page.locator("#catalogue-assistant").getByRole("link", { name: "Browse plugins" }).click();
  await expect(page).toHaveURL(/plugins\/index.html$/);
  await expect(page.getByRole("link", { name: /Spotless/ }).first()).toBeVisible();
});

test("submits only question, preserves bounded turns and verified source navigation", async ({ page }) => {
  const submissions = [];
  await page.route("https://assistant.example/assistant/chat", async route => {
    if (route.request().method() === "OPTIONS") {
      return route.fulfill({ status: 204, headers: {
        "Access-Control-Allow-Origin": "http://localhost:8080",
        "Access-Control-Allow-Methods": "POST",
        "Access-Control-Allow-Headers": "content-type,hx-request,hx-target,hx-trigger,hx-trigger-name"
      } });
    }
    submissions.push(route.request().postData());
    await new Promise(resolve => setTimeout(resolve, 150));
    return response(route, '<article class="assistant-turn"><p>Catalogue evidence</p><a href="https://teggr.github.io/ci-ready-maven/plugins/spotless-maven-plugin.html">Spotless Maven Plugin</a></article>');
  });
  await enabled(page);
  await page.getByRole("button", { name: "Spotless configuration", exact: true }).click();
  const question = page.getByRole("textbox", { name: "Your catalogue question" });
  await expect(question).toBeFocused();
  for (let i = 0; i < 3; i++) {
    await page.getByRole("button", { name: "Ask the catalogue" }).click();
    await expect(page.locator("#assistant-loading")).toBeVisible();
    await expect(page.locator("#assistant-status")).toHaveText(/Response received/);
  }
  await expect(page.locator("#assistant-transcript > *")).toHaveCount(2);
  expect(submissions).toHaveLength(3);
  for (const submission of submissions) {
    expect([...new URLSearchParams(submission).keys()]).toEqual(["question"]);
  }
  await expect(question).toHaveValue("Show me the documented Maven configuration for Spotless.");
  await expect(page.locator("#assistant-transcript a").first()).toHaveAttribute("href",
    "https://teggr.github.io/ci-ready-maven/plugins/spotless-maven-plugin.html");
  expect(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth)).toBeTruthy();
});

test("network error and rate limit retain question for retry", async ({ page }) => {
  let failed = false;
  await page.route("https://assistant.example/assistant/chat", async route => {
    if (route.request().method() === "OPTIONS") return route.fulfill({ status: 204, headers: {
      "Access-Control-Allow-Origin": "http://localhost:8080",
      "Access-Control-Allow-Headers": "content-type,hx-request,hx-target,hx-trigger,hx-trigger-name",
      "Access-Control-Allow-Methods": "POST"
    } });
    if (!failed) { failed = true; return route.abort(); }
    return response(route, "Please wait", 429);
  });
  await enabled(page);
  const question = page.getByRole("textbox", { name: "Your catalogue question" });
  await question.fill("Find JaCoCo coverage");
  await page.getByRole("button", { name: "Ask the catalogue" }).click();
  await expect(page.locator("#assistant-status")).toHaveText(/unavailable/);
  await expect(question).toHaveValue("Find JaCoCo coverage");
  await page.getByRole("button", { name: "Ask the catalogue" }).click();
  await expect(page.locator("#assistant-status")).toHaveText(/Too many requests/);
  await expect(question).toHaveValue("Find JaCoCo coverage");
});

test("timeout restores a usable retry state", async ({ page }) => {
  await page.route("**/js/assistant-config.js", route => route.fulfill({
    contentType: "application/javascript",
    body: 'window.catalogueAssistant = { endpoint: "http://localhost:8080/assistant/chat", requestTimeoutMillis: 1000 };'
  }));
  await page.route("**/assistant/chat", async route => {
    await new Promise(resolve => setTimeout(resolve, 2000));
    await response(route, "<article>Late response</article>").catch(() => {});
  });
  await page.goto("/");
  await page.getByRole("textbox", { name: "Your catalogue question" }).fill("Find Spotless");
  await page.getByRole("button", { name: "Ask the catalogue" }).click();
  await expect(page.locator("#assistant-status")).toHaveText(/unavailable/);
  await expect(page.getByRole("button", { name: "Ask the catalogue" })).toBeEnabled();
  await expect(page.getByRole("textbox", { name: "Your catalogue question" })).toHaveValue("Find Spotless");
});
