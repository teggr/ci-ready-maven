---
title: Maven JDeps Plugin
description: Analyze compiled bytecode dependencies and detect internal JDK API usage.
order: 12
slug: maven-jdeps-plugin
name: Maven JDeps Plugin
category: Static Analysis
type: Maven Plugin
mavenCoordinates: org.apache.maven.plugins:maven-jdeps-plugin
lastRelease: "3.2.0"
learnMoreText: Maven JDeps Plugin Documentation
learnMoreHref: https://maven.apache.org/plugins/maven-jdeps-plugin/
tags:
  - Static Analysis
  - Java Modules
  - Dependency Analysis
dateAdded: 2026-10-24
---

The Maven JDeps Plugin runs the JDK `jdeps` tool against compiled project classes to analyze module and package dependencies. It detects use of internal JDK APIs such as `sun.*` packages and can report on multi-release JARs. This helps validate Java module system compatibility and prevents builds from relying on private APIs that may be removed in future Java releases.

## Code Example

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-jdeps-plugin</artifactId>
    <version>3.2.0</version>
    <executions>
        <execution>
            <goals>
                <goal>jdkinternals</goal>
                <goal>test-jdkinternals</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <failOnWarning>true</failOnWarning>
    </configuration>
</plugin>
```
