---
title: JDepend Maven Plugin
description: Generate package design metrics reports during Maven site generation.
order: 12
slug: jdepend-maven-plugin
name: JDepend Maven Plugin
category: Static Analysis
type: Maven Plugin
mavenCoordinates: org.codehaus.mojo:jdepend-maven-plugin
lastRelease: "2.2.1"
learnMoreText: JDepend Maven Plugin Documentation
learnMoreHref: https://www.mojohaus.org/jdepend-maven-plugin/
tags:
  - Static Analysis
  - Design Metrics
  - Package Coupling
dateAdded: 2026-10-24
---

The JDepend Maven Plugin analyzes compiled Java classes and generates package-level design metrics as part of Maven site reporting. It reports afferent and efferent coupling, abstractness, instability, and distance from the main sequence. These metrics help teams identify tightly coupled packages and track architectural quality trends over time.

## Code Example

```xml
<reporting>
    <plugins>
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>jdepend-maven-plugin</artifactId>
            <version>2.2.1</version>
        </plugin>
    </plugins>
</reporting>
```
