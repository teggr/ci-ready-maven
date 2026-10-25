# CI Ready Maven

A catalogue of Maven plugins and utilities for building robust CI pipelines and quality gates in enterprise Java projects.

Live site: **https://teggr.github.io/ci-ready-maven/**

## What's In Here

This is a Maven multi-module project. The root `pom.xml` is the parent and aggregator (`io.github.teggr:ci-ready-maven`). The `static-site/` module (`io.github.teggr:ci-ready-maven-static-site`) contains the existing site:

```text
pom.xml
static-site/
    pom.xml
    site-config.xml
    _index.md
    _guides/
    _plugins/
    _static/
qa-api/
    pom.xml
    src/
```

Future modules can be added alongside `static-site/` and registered in the root POM's `<modules>` list. Site generation is configured only in the static-site module, not inherited by siblings.

The `qa-api/` module is a stateless Spring Boot question-and-answer API. It indexes `static-site/_plugins/` during the Maven build and packages the generated catalogue index with the application. See [`qa-api/README.md`](qa-api/README.md) for configuration, local development, API usage, resource limits and evaluation details. The API does not change static-site generation or deployment.

The homepage's **Ask the catalogue** form sends independent questions to `POST /api/v1/chat`. Configure the service origin in the `data-api-base-url` attribute on the homepage component in `static-site/_index.md`; it is intentionally empty until an API deployment URL is chosen. An empty value disables the question input, submit button and suggested questions, displays an availability message, and prevents API requests. Set an HTTPS origin and regenerate the site to enable it for a deployed service.

For local integration testing, use separate ports for the site and API. Set `data-api-base-url` to `http://localhost:8081`, configure `QA_CORS_ALLOWED_ORIGIN` as `http://localhost:8080`, and run the API on port `8081`. Generate the preview with `-Delectrostatic.baseUrl=http://localhost:8080` before starting Electrostatic's `serve` goal with the same property; this makes the generated asset paths resolve under the local preview origin.

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
| --- | --- |
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

The site is generated with the [Electrostatic](https://github.com/teggr/electrostatic) Maven plugin (version `0.0.5`), bound to the static-site module's `generate-resources` phase. Content is sourced from `static-site/_guides/` and `static-site/_plugins/` and rendered to `static-site/target/generated-site/`.

Build all modules from the repository root:

```bash
./mvnw -B -ntp clean verify
```

To generate only the site, run `mvn -B -ntp -pl static-site generate-resources`. For local preview, also run from the repository root:

```bash
mvn -pl static-site io.github.teggr:electrostatic-maven-plugin:0.0.5:serve -Delectrostatic.baseUrl=http://localhost:8080 -Delectrostatic.watch=true
```

The Maven configuration in `static-site/pom.xml` points Electrostatic at that module's directory and writes output to `static-site/target/generated-site/`. GitHub Actions runs the root reactor build and uploads only that generated directory to GitHub Pages on every push to `main` (or a manual workflow run). The published site remains at **https://teggr.github.io/ci-ready-maven/**; the module folder is not part of the public URL.


## Electrostatic

The site uses Electrostatic's docs theme, configured in `static-site/site-config.xml`, with catalogue pages in `static-site/_plugins/`, guides in `static-site/_guides/`, and CSS overrides in `static-site/_static/`. The Maven plugin automatically includes local CSS overrides after the theme stylesheets.

## Contributing

To add a new Maven tool, create a new Markdown file in `static-site/_plugins/` following the pattern of the existing files. Open a pull request; the site will be rebuilt and deployed once the change is merged into `main`.
