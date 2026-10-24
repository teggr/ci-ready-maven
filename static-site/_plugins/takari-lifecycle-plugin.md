---
title: Takari Lifecycle Plugin
description: Incremental compilation and packaging for faster Maven builds.
order: 12
slug: takari-lifecycle-plugin
name: Takari Lifecycle Plugin
category: CI Extensions / Maven Extensions for Build Time
type: Maven Plugin / Extension (Takari)
mavenCoordinates: io.takari.maven.plugins:takari-lifecycle-plugin
lastRelease: "2.3.4"
learnMoreText: Takari Lifecycle Plugin on GitHub
learnMoreHref: https://github.com/takari/takari-lifecycle
tags:
  - Build
  - Incremental Compilation
  - Build Speed
  - Extensions
dateAdded: 2026-10-24
---

The Takari Lifecycle Plugin is a drop-in replacement for Maven's default lifecycle that adds incremental compilation and packaging. It recompiles changed files and rebuilds JARs only when their contents change, helping reduce build times in large projects. Version 2.3.0 and later requires Java 17 or newer to run. For additional multi-module scheduling improvements, it can be paired with Takari Smart Builder.

## Code Example

```xml
<plugin>
    <groupId>io.takari.maven.plugins</groupId>
    <artifactId>takari-lifecycle-plugin</artifactId>
    <version>2.3.4</version>
    <extensions>true</extensions>
</plugin>
```
