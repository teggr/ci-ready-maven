---
title: JFR Maven Extension
description: Profile Maven build performance with Java Flight Recorder events per phase and mojo.
order: 12
slug: jfr-maven-extension
name: JFR Maven Extension
category: CI Extensions / Maven Extensions for Build Time
type: Maven Extension
mavenCoordinates: com.github.marschall:jfr-maven-extension
lastRelease: "0.1.1"
learnMoreText: JFR Maven Extension on GitHub
learnMoreHref: https://github.com/marschall/jfr-maven-extension
tags:
  - CI
  - Build Performance
  - JFR
  - Java Flight Recorder
  - Maven Extension
dateAdded: 2026-08-02
---

The JFR Maven Extension instruments Maven builds with Java Flight Recorder (JFR) events, recording each Maven phase and mojo execution as a structured JFR event. The resulting `.jfr` recording can be opened in JDK Mission Control (`jmc`) for deep performance analysis, including GC pressure, JIT compilation, I/O bottlenecks, and CPU profiling across the entire build lifecycle. This makes it straightforward to identify which plugins or phases dominate build time and to correlate Maven activity with JVM-level metrics. The extension requires Java 11 or later, where JFR is bundled as part of OpenJDK at no extra cost.

## Code Example

```xml
<!-- .mvn/extensions.xml -->
<extensions>
    <extension>
        <groupId>com.github.marschall</groupId>
        <artifactId>jfr-maven-extension</artifactId>
        <version>0.1.1</version>
    </extension>
</extensions>
```
