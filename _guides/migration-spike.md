---
title: Electrostatic Migration
description: How CI Ready Maven builds and previews its Electrostatic docs site.
order: 1
slug: migration-spike
---

CI Ready Maven uses the Electrostatic docs theme to render its plugin catalogue and guides. The Electrostatic Maven plugin reads the repository root, including `_plugins/`, `_guides/`, `_static/`, and `site-config.xml`, and writes the generated site to `target/generated-site/`. Use the Maven `generate` goal to build the site or the `serve` goal to preview it locally.
