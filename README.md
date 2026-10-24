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
| JMeter Maven Plugin | Performance Testing |
| Maven Changelog Plugin | Git / Reporting |
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
| Maven SCM Plugin | Git / CI |
| git-commit-id-maven-plugin | Git / CI |
| Maven Semantic Gitlog Plugin | Dependency Governance / Build Reproducibility |
| Fabric8 Docker Maven Plugin | Docker / Integration Testing |
| Jib Maven Plugin | Docker / Container / OCI / CI Build |
| Spring Boot Maven Plugin | Docker / Build |
| Spotify dockerfile-maven-plugin | Docker |
| Clirr Maven Plugin | Static Analysis |
| TagList Maven Plugin | Quality / Reporting |
| Develocity Maven Extension | CI Extensions / Maven Extensions for Build Time |
| Maven Build Cache Extension | CI Extensions / Maven Extensions for Build Time |
| Maven Timeline | CI Extensions / Maven Extensions for Build Time |
| Maven Notifier | CI Extensions / Developer Experience |
| Takari Smart Builder | Dependency Governance / Build Reproducibility |
| Polyglot Maven | Dependency Governance / Build Reproducibility |

## Building the Site

The site is generated with the [Electrostatic](https://github.com/teggr/electrostatic) Maven plugin (version `0.0.5`). Content is sourced from the root-level section folders (`_guides/` and `_plugins/`) and rendered to `target/generated-site/`.

```bash
mvn -B -ntp io.github.teggr:electrostatic-maven-plugin:0.0.5:generate
```

For local preview:

```bash
mvn io.github.teggr:electrostatic-maven-plugin:0.0.5:serve -Delectrostatic.baseUrl=http://localhost:8080
```

The Maven configuration in `pom.xml` points Electrostatic at the repository root and writes output to `target/generated-site/`. GitHub Actions builds and deploys that directory to GitHub Pages on every push to `main`.


## Electrostatic

The site uses Electrostatic's docs theme, configured in `site-config.xml`, with catalogue pages in `_plugins/`, guides in `_guides/`, and CSS overrides in `_static/`. The Maven plugin automatically includes local CSS overrides after the theme stylesheets.

## Contributing

To add a new Maven tool, create a new Markdown file in `_plugins/` following the pattern of the existing files. Open a pull request and the site will be rebuilt automatically.
