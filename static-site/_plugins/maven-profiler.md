---
title: Maven Profiler
description: Records execution time of every mojo in the Maven build lifecycle to spot CI bottlenecks.
order: 12
slug: maven-profiler
name: Maven Profiler
category: CI Extensions / Maven Extensions for Build Time
type: Maven Extension (jcgay)
mavenCoordinates: fr.jcgay.maven:maven-profiler
lastRelease: "3.2"
learnMoreText: Maven Profiler on GitHub
learnMoreHref: https://github.com/jcgay/maven-profiler
tags:
  - CI
  - Build Performance
  - Profiling
  - Maven Extension
dateAdded: 2026-08-02
---

Maven Profiler is a Maven extension by Jean-Christophe Gay that records the execution time of every mojo in the build lifecycle. At the end of the build it prints a sorted table showing which plugins and goals consumed the most time, making it easy to spot the mojos slowing down a CI pipeline. It can be enabled as a core extension via `.mvn/extensions.xml` or activated on demand with the `-Dprofile` command-line flag, and it works correctly with multi-threaded (`-T`) builds. Reports can be generated as `HTML` (default), `JSON`, or `CONSOLE`, and multiple formats can be combined via `-DprofileFormat=JSON,HTML,CONSOLE`.

## Code Example

```xml
<!-- .mvn/extensions.xml -->
<extensions>
    <extension>
        <groupId>fr.jcgay.maven</groupId>
        <artifactId>maven-profiler</artifactId>
        <version>3.2</version>
    </extension>
</extensions>
```

Enable profiling for a build:

```bash
mvn clean install -Dprofile
```
