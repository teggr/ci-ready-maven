# Electrostatic migration mapping and framework gaps

## Scope

This document maps the current JBang generator (`/build.java`) to Electrostatic docs theme capabilities and captures migration gaps discovered during implementation.

## Feature mapping

| Current feature (`build.java`) | Electrostatic docs theme status | Classification | Notes |
|---|---|---|---|
| Root markdown catalogue files with YAML front matter | Markdown in section folders (`_plugins`, `_guides`, etc.) | Configurable | Content can be migrated while keeping existing front matter fields plus docs fields (`title`, `description`, `order`, `slug`). |
| Tool detail pages | One page per markdown file | Native | Docs plugin emits `/docs/{section}/{slug}.html`. |
| Landing/index page | `DocsLandingPage` | Native | Generated index exists, but content/layout differs from CI Ready Maven custom hero and sections. |
| Section index pages | Generated per docs section | Native | Equivalent to grouped list pages, not card-rich catalog views. |
| Tag pages (`tag-*.html`) | No built-in docs tag aggregation | Missing | Requires custom `AggregatorPlugin` in Electrostatic core or pre-generated static pages. |
| Sorted views (`grid-alphabetical`, `grid-by-tag`, `grid-by-date`) | No built-in derived partials | Missing | Needs custom generation extensions; docs theme outputs full pages only. |
| HTMX partial swap endpoints and OOB button swaps | No built-in HTMX partial rendering model | Missing | Would require custom pages/files and frontend behavior outside stock docs theme. |
| SEO metadata parity (OG/Twitter/canonical per tool/tag) | Basic page metadata from theme/layout | Needs extension | Current CI Ready Maven has explicit per-page SEO model in `build.java`. |
| Deterministic tag slugging (`tagToSlug`) | Docs pages support slug, no tag slug pipeline | Needs extension | Need a custom taxonomy implementation to keep exact behavior. |
| Front matter schema enforcement for tool fields | Docs parser tolerates optional fields | Missing | Validation must be added as a pre-build check or custom content plugin behavior. |

## Spike contents added

- `/electrostatic/site-config.xml` docs-theme configuration
- `/electrostatic/_plugins/*.md` (3 migrated sample tool pages)
- `/electrostatic/_guides/migration-spike.md`
- `/electrostatic/_static/css/styles.css`

## Blockers and constraints

1. **Distribution blocker**: `site.electrostatic:electrostatic-maven-plugin:0.0.1-SNAPSHOT` was not resolvable from Maven repositories during validation.
2. **Runtime blocker in this environment**: `jbang` is not installed locally, so JBang-based commands cannot currently run here.
3. **Capability gaps**: taxonomy/tag pages, sortable derived views, and HTMX partial rendering are not available out of the box in docs theme.

## Recommendation

Proceed with Electrostatic only if one of these is accepted:

- extend Electrostatic with custom plugins for taxonomy + derived views + SEO parity, or
- simplify CI Ready Maven UX to align with stock docs-theme capabilities.

Until then, keep `/build.java` as production generator and treat `/electrostatic` as migration spike input.
