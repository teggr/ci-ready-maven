---
title: Develocity Maven Extension
description: Build scans, remote caching, and test acceleration capabilities for Maven CI.
order: 12
slug: develocity-maven-extension
name: Develocity Maven Extension
category: CI Extensions
type: Maven Extension (Gradle Inc.)
mavenCoordinates: com.gradle:develocity-maven-extension
lastRelease: "2.4.0"
learnMoreText: Develocity Maven Extension Documentation
learnMoreHref: https://docs.gradle.com/develocity/maven-extension/
tags:
  - CI
  - Build Speed
  - Build Cache
  - Build Analytics
  - Maven Extension
dateAdded: 2026-06-06
---

The Develocity Maven Extension captures rich Build Scan data for every Maven build and can publish scans to the free scans.gradle.com service or to a self-hosted Develocity server. It also enables advanced acceleration features such as local and remote build caching, predictive test selection, and distributed test execution to reduce CI cycle time. Teams can start with free build analytics and adopt commercial Develocity capabilities for shared caching and test optimization at scale.

## Code Example

```xml
<extensions>
    <extension>
        <groupId>com.gradle</groupId>
        <artifactId>develocity-maven-extension</artifactId>
        <version>2.4.0</version>
    </extension>
</extensions>
```
