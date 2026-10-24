---
title: Maven Wrapper Plugin
description: Generate Maven Wrapper scripts to pin the Maven version used across developer machines and CI.
order: 12
slug: maven-wrapper-plugin
name: Maven Wrapper Plugin
category: CI / Build
type: Maven Plugin (Apache)
mavenCoordinates: org.apache.maven.plugins:maven-wrapper-plugin
lastRelease: "3.3.4"
learnMoreText: Maven Wrapper Documentation
learnMoreHref: https://maven.apache.org/wrapper/
tags:
  - CI
  - Build Reproducibility
  - Maven Wrapper
dateAdded: 2026-10-24
---

The Apache Maven Wrapper Plugin generates the `mvnw` and `mvnw.cmd` scripts and the `.mvn/wrapper/maven-wrapper.properties` file. Pinning the Maven distribution ensures developers and CI environments use the same Maven version without requiring a system-wide Maven installation. Run `mvn wrapper:wrapper -Dmaven=3.9.9` once to set up the wrapper, then use `./mvnw` in CI and on developer machines. Commit the generated wrapper scripts and properties file so every checkout can use the pinned Maven version.

## Code Example

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-wrapper-plugin</artifactId>
    <version>3.3.4</version>
</plugin>
```
