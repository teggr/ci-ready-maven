---
title: Maven Build Cache Extension
description: Speed up CI with incremental Maven builds and local/remote cache reuse.
order: 12
slug: maven-build-cache-extension
name: Maven Build Cache Extension
category: CI Extensions / Maven Extensions for Build Time
type: Maven Extension (Apache)
mavenCoordinates: org.apache.maven.extensions:maven-build-cache-extension
lastRelease: "1.2.3"
learnMoreText: Maven Build Cache Extension Documentation
learnMoreHref: https://maven.apache.org/extensions/maven-build-cache-extension/
tags:
  - CI
  - Build Speed
  - Incremental Build
  - Cache
  - Maven Extension
dateAdded: 2026-06-06
---

The Maven Build Cache Extension is the official Apache Maven extension for incremental builds and build caching. It fingerprints module inputs such as sources, dependencies, and plugin configuration, then restores previously produced outputs when nothing relevant changed. This allows unchanged modules to be skipped, reducing build time in multi-module projects. The extension supports local cache usage and shared remote cache backends so CI agents can reuse build outputs across runs.

## Code Example

```xml
<!-- .mvn/extensions.xml -->
<extensions>
    <extension>
        <groupId>org.apache.maven.extensions</groupId>
        <artifactId>maven-build-cache-extension</artifactId>
        <version>1.2.3</version>
    </extension>
</extensions>
```

```xml
<!-- .mvn/maven-build-cache-config.xml -->
<cache xmlns="http://maven.apache.org/BUILD-CACHE-CONFIG/1.0.0">
    <local>
        <enabled>true</enabled>
    </local>
    <remote>
        <enabled>true</enabled>
        <url>https://cache.example.com/maven-build-cache/</url>
    </remote>
</cache>
```
