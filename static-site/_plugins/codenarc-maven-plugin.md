---
title: CodeNarc Maven Plugin
description: Groovy source analysis for CI quality gates.
order: 12
slug: codenarc-maven-plugin
name: CodeNarc Maven Plugin
category: Static Analysis
type: Maven Plugin
mavenCoordinates: org.codehaus.mojo:codenarc-maven-plugin
lastRelease: "0.22-1"
learnMoreText: CodeNarc Maven Plugin Documentation
learnMoreHref: https://www.mojohaus.org/codenarc-maven-plugin/
tags:
  - Static Analysis
  - Groovy
  - Code Quality
dateAdded: 2026-10-24
---

The CodeNarc Maven Plugin analyzes Groovy source code for defects, bad practices, inconsistencies, and style issues. It brings CodeNarc into Maven builds as a quality gate for Groovy applications and Groovy-based tests, including Spock projects. CodeNarc targets Groovy and does not analyze Java source code.

## Code Example

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>codenarc-maven-plugin</artifactId>
    <version>0.22-1</version>
    <executions>
        <execution>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
