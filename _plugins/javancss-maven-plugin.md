---
title: JavaNCSS Maven Plugin
description: Measure NCSS and cyclomatic complexity as part of Maven site reporting.
order: 12
slug: javancss-maven-plugin
name: JavaNCSS Maven Plugin
category: Quality / Static Analysis
type: Maven Plugin
mavenCoordinates: org.codehaus.mojo:javancss-maven-plugin
lastRelease: "2.1"
learnMoreText: JavaNCSS Maven Plugin Documentation
learnMoreHref: https://www.mojohaus.org/javancss-maven-plugin/
tags:
  - Quality
  - Complexity Metrics
  - Code Metrics
  - Reporting
dateAdded: 2026-06-06
---

The JavaNCSS Maven Plugin measures Java source complexity with JavaNCSS (Java Non Commenting Source Statements). It reports NCSS counts, cyclomatic complexity (McCabe), and method/class/package size metrics so teams can identify overly complex code that is harder to test and maintain. As a reporting plugin, its metrics are generated as part of the Maven site.

## Code Example

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>javancss-maven-plugin</artifactId>
    <version>2.1</version>
    <configuration>
        <ncssLimit>200</ncssLimit>
        <ccnLimit>10</ccnLimit>
        <failOnViolation>true</failOnViolation>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
