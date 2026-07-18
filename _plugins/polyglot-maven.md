---
title: Polyglot Maven
description: Use non-XML POM dialects while keeping standard Maven CI behavior.
order: 12
slug: polyglot-maven
name: Polyglot Maven
category: Dependency Governance
type: Maven Plugin
mavenCoordinates: io.takari.polyglot:polyglot-kotlin
lastRelease: "0.8.0"
learnMoreText: Polyglot Maven on GitHub
learnMoreHref: https://github.com/takari/polyglot-maven
tags:
  - Dependency Governance
  - Build Reproducibility
dateAdded: 2026-07-18
---

Polyglot Maven is a set of Takari Maven extensions that lets teams author Maven models in dialects such as Groovy, Kotlin, Scala, YAML, Ruby, and Atom instead of raw XML. At build time, the selected extension translates the dialect-specific file into Maven's internal model, so standard Maven lifecycle behavior and CI quality gates remain unchanged. This is useful for teams that prefer DSL-based build configuration but still want to run native Maven tooling, plugins, and enterprise pipeline controls. Adoption is relatively niche, but it is a practical option for Groovy- and Kotlin-heavy engineering organizations standardizing on Maven CI.

## Code Example

```xml
<!-- .mvn/extensions.xml -->
<extensions>
    <!-- Select the dialect artifact matching your pom.* file (for example: polyglot-groovy, polyglot-yaml) -->
    <extension>
        <groupId>io.takari.polyglot</groupId>
        <artifactId>polyglot-kotlin</artifactId>
        <version>0.8.0</version>
    </extension>
</extensions>
```
