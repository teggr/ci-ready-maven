---
title: CI Ready Maven
description: A practical catalogue of Maven plugins and tools for enterprise CI quality gates.
---

CI Ready Maven helps teams choose the right Maven plugins for reliable quality gates in enterprise CI pipelines.

## What You Can Find Here

- Curated plugin guidance for static analysis, dependency governance, coverage, security, and source policy
- Practical Maven configuration snippets you can copy into real builds
- Category and tag-driven browsing so teams can standardize CI controls quickly

## Quick Start

1. Browse the [Plugins](/plugins/index.html) catalogue and pick the checks your pipeline needs.
2. Use each plugin page's Code Example as a baseline configuration.
3. Add team-specific thresholds and policy settings in your parent POM.

## Explore the Docs

- [Plugins](/plugins/index.html): Tool-by-tool guidance and CI-focused configuration examples.
- [Guides](/guides/index.html): Broader implementation notes and migration context.

## Why CI Ready Maven

Most teams need consistent quality gates across many repositories. This catalogue is designed to reduce drift by making plugin choices and baseline configurations easy to discover, compare, and adopt.

<section id="catalogue-assistant" class="catalogue-assistant" aria-labelledby="assistant-heading">
  <h2 id="assistant-heading">Catalogue assistant</h2>
  <p id="assistant-scope">Find documented tools and configuration examples. Answers use only this catalogue, which may be out of date. Ask each question in full; earlier turns are not sent as evidence.</p>
  <p>When enabled, questions are sent to a hosted AI provider. Do not include secrets or personal information. This page keeps a short transcript until you reload; the service does not store conversations.</p>
  <p id="assistant-availability" role="status">The assistant is not currently enabled. You can still browse the catalogue below.</p>
  <form id="assistant-form" method="post" hx-target="#assistant-transcript" hx-swap="beforeend" hx-indicator="#assistant-loading" hx-disabled-elt="#assistant-submit" hx-request='{"timeout":25000}' aria-describedby="assistant-scope">
    <label for="assistant-question">Your catalogue question</label>
    <textarea id="assistant-question" name="question" rows="3" maxlength="1000" required disabled></textarea>
    <button id="assistant-submit" type="submit" disabled>Ask the catalogue</button>
    <span id="assistant-loading" class="htmx-indicator" role="status">Looking up catalogue evidence…</span>
  </form>
  <div class="assistant-starters" aria-label="Suggested questions">
    <button type="button" data-question="Which plugins can enforce Java code formatting?" disabled>Java formatting</button>
    <button type="button" data-question="How can I measure test coverage in Maven?" disabled>Test coverage</button>
    <button type="button" data-question="Compare the static-analysis plugins in this catalogue." disabled>Static analysis</button>
    <button type="button" data-question="Show me the documented Maven configuration for Spotless." disabled>Spotless configuration</button>
    <button type="button" data-question="Which plugins are relevant to dependency governance?" disabled>Dependency governance</button>
  </div>
  <div id="assistant-transcript" class="assistant-transcript" role="log" aria-label="Catalogue conversation" aria-live="polite" aria-relevant="additions" tabindex="0"></div>
  <p id="assistant-status" role="status"></p>
  <p><a href="plugins/index.html">Browse plugins</a> or <a href="guides/index.html">read the guides</a> instead.</p>
  <noscript><p>The assistant requires JavaScript. All catalogue pages remain available without it.</p></noscript>
</section>
<script src="https://cdn.jsdelivr.net/npm/htmx.org@2.0.8/dist/htmx.min.js" integrity="sha384-/TgkGk7p307TH7EXJDuUlgG3Ce1UVolAOFopFekQkkXihi5u/6OCvVKyz1W+idaz" crossorigin="anonymous" defer></script>
<script src="js/assistant-config.js" defer></script>
<script src="js/assistant.js" defer></script>