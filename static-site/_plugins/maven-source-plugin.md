---
title: Maven Source Plugin
description: Bundle project sources into a sources JAR for publication and IDE navigation.
order: 12
slug: maven-source-plugin
name: Maven Source Plugin
category: Build / CI
type: Maven Plugin (Apache)
mavenCoordinates: org.apache.maven.plugins:maven-source-plugin
lastRelease: "3.4.0"
learnMoreText: Maven Source Plugin Documentation
learnMoreHref: https://maven.apache.org/plugins/maven-source-plugin/
tags:
  - Build
  - Sources JAR
  - Maven Central
  - Developer Experience
dateAdded: 2026-10-24
---

The Maven Source Plugin bundles a project's source files into a `-sources.jar` artifact. Publishing this artifact alongside the compiled JAR is required for Maven Central publication and enables IDE source navigation. In CI, bind the `jar-no-fork` goal to the `package` phase so the sources JAR is attached to the project artifact and deployed alongside the main JAR.

## Code Example

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-source-plugin</artifactId>
    <version>3.4.0</version>
    <executions>
        <execution>
            <id>attach-sources</id>
            <phase>package</phase>
            <goals>
                <goal>jar-no-fork</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
