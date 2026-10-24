---
title: Maven Dependency Plugin
description: Analyze and manage project dependencies in Maven builds.
order: 12
slug: maven-dependency-plugin
name: Maven Dependency Plugin
category: Dependency Governance
type: Maven Plugin
mavenCoordinates: org.apache.maven.plugins:maven-dependency-plugin
lastRelease: "3.11.0"
learnMoreText: Maven Dependency Plugin Documentation
learnMoreHref: https://maven.apache.org/plugins/maven-dependency-plugin/
tags:
  - Dependency Governance
dateAdded: 2026-10-24
---

The Maven Dependency Plugin provides goals for analyzing and managing project dependencies. Use `dependency:tree` to inspect transitive dependencies and diagnose conflicts, or `dependency:copy-dependencies` and `dependency:unpack` to collect dependency artifacts. Its `dependency:analyze` goal detects unused and undeclared dependencies, making it useful as a CI quality gate. Configure `failOnWarning` to fail the build when dependency warnings are found; review results for dependencies used through reflection or other mechanisms bytecode analysis cannot detect.

## Code Example

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-dependency-plugin</artifactId>
    <version>3.11.0</version>
    <executions>
        <execution>
            <id>analyze-dependencies</id>
            <phase>verify</phase>
            <goals>
                <goal>analyze</goal>
            </goals>
            <configuration>
                <failOnWarning>true</failOnWarning>
            </configuration>
        </execution>
    </executions>
</plugin>
```
