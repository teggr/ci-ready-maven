---
title: Cobertura Maven Plugin
description: Legacy bytecode instrumentation coverage plugin for older Java CI pipelines.
order: 12
slug: cobertura-maven-plugin
name: Cobertura Maven Plugin
category: Coverage
type: Maven Plugin (MojoHaus)
mavenCoordinates: org.codehaus.mojo:cobertura-maven-plugin
lastRelease: "2.7"
learnMoreText: Cobertura Maven Plugin Documentation
learnMoreHref: https://www.mojohaus.org/cobertura-maven-plugin/
tags:
  - Coverage
  - Legacy
dateAdded: 2026-06-06
---

The Cobertura Maven Plugin instruments Java classes to measure line and branch coverage and generates HTML and XML reports for CI visibility. It remains present in some older CI pipelines, but Cobertura is no longer actively maintained and does not handle newer Java language features such as lambdas and streams reliably. For modern builds, JaCoCo (`org.jacoco:jacoco-maven-plugin`) is the recommended replacement.

## Code Example

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>cobertura-maven-plugin</artifactId>
    <version>2.7</version>
    <configuration>
        <formats>
            <format>html</format>
            <format>xml</format>
        </formats>
        <check/>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>cobertura</goal>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
