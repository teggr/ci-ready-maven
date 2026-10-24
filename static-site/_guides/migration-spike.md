---
title: Electrostatic Migration
description: How CI Ready Maven builds and previews its Electrostatic docs site.
order: 1
slug: migration-spike
---

CI Ready Maven uses the Electrostatic docs theme to render its plugin catalogue and guides. The repository root is a Maven parent and aggregator (`ci-ready-maven`), with the site in the `static-site/` module (`ci-ready-maven-static-site`). The Electrostatic Maven plugin reads that module's directory, including `_plugins/`, `_guides/`, `_static/`, and `site-config.xml`, and writes the generated site to `static-site/target/generated-site/`.

Run `mvn -B -ntp clean verify` from the repository root to build all modules and generate the site, or `mvn -B -ntp -pl static-site generate-resources` to generate only the site. For local preview, run `mvn -pl static-site io.github.teggr:electrostatic-maven-plugin:0.0.5:serve -Delectrostatic.baseUrl=http://localhost:8080`. GitHub Actions publishes only the static-site module's generated output to GitHub Pages, so the public site URLs remain unchanged.
