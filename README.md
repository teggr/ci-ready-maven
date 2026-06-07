# CI Ready Maven

A catalogue of Maven plugins and utilities for building robust CI pipelines and quality gates in enterprise Java projects.

Live site: **https://teggr.github.io/ci-ready-maven/**

## What's In Here

Each tool is documented as a Markdown file with YAML front matter containing:

- `name` – display name
- `category` – e.g. Static Analysis, Coverage, Dependency Governance
- `type` – e.g. Maven Plugin, CLI Tool
- `mavenCoordinates` – e.g. `org.jacoco:jacoco-maven-plugin`
- `lastRelease` – latest known version
- `learnMoreHref` / `learnMoreText` – link to official docs
- `tags` – used for filtering on the site
- `dateAdded` – ISO date

The body of each file is a description followed by a `## Code Example` section with a ready-to-use Maven configuration snippet.

## Tools Catalogued

| Tool | Category |
|------|----------|
| Maven Enforcer Plugin | Dependency Governance / Build Reproducibility |
| SpotBugs Maven Plugin | Static Analysis |
| PMD Maven Plugin | Static Analysis |
| Animal Sniffer Maven Plugin | Static Analysis |
| JavaNCSS Maven Plugin | Static Analysis |
| JaCoCo Maven Plugin | Coverage |
| Cobertura Maven Plugin | Coverage (Legacy) |
| google-java-format | Source Code Policy / Formatting |
| Palantir Java Format | Source Code Policy / Formatting |
| Spotless Maven Plugin | Source Code Policy / Formatting |
| SortPOM Maven Plugin | Source Code Policy / Formatting |
| Tidy Maven Plugin | Source Code Policy / Formatting |
| JHarmonizer | Source Code Policy / Source Structure |
| License Maven Plugin | Security and Compliance |
| Maven JarSigner Plugin | Security and Compliance / CI |
| OWASP Dependency-Check | Security and Compliance / Dependency Governance |
| TagList Maven Plugin | Quality / Reporting |
| Develocity Maven Extension | CI Extensions / Maven Extensions for Build Time |
| Maven Build Cache Extension | CI Extensions / Maven Extensions for Build Time |
| Takari Smart Builder | Dependency Governance / Build Reproducibility |

## Building the Site

The site is generated with [Electrostatic](https://electrostatic.github.io/) via JBang. Content is sourced from section folders (`_guides/` and `_plugins/`) and rendered to `generated-site/`.

```bash
jbang --fresh site.electrostatic:electrostatic-cli:0.0.3 build
```

For local preview:

```bash
jbang --fresh site.electrostatic:electrostatic-cli:0.0.3 serve --base-url=http://localhost:8080
```

GitHub Actions builds and deploys `generated-site/` to GitHub Pages on every push to `main`.


## Electrostatic Migration Spike

This repository now runs on the Electrostatic docs theme.

It includes:

- docs-theme `site-config.xml`
- plugin catalogue pages in `_plugins/`
- guide pages in `_guides/`
- static CSS overrides in `_static/css/`

Current note: if no explicit homepage content file is provided, the docs-theme landing page uses default boilerplate copy.

## Contributing

To add a new Maven tool, create a new Markdown file in `_plugins/` following the pattern of the existing files. Open a pull request and the site will be rebuilt automatically.
