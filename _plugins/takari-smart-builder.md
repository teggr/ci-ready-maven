---
title: Takari Smart Builder
description: Dependency-critical-path-aware Maven module scheduling for faster parallel CI builds.
order: 12
slug: takari-smart-builder
name: Takari Smart Builder
category: Dependency Governance
type: Maven Extension (Takari)
mavenCoordinates: io.takari.maven:takari-smart-builder
lastRelease: "1.1.0"
learnMoreText: Takari Smart Builder on GitHub
learnMoreHref: https://github.com/takari/takari-smart-builder
tags:
  - Dependency Governance
  - Build Reproducibility
dateAdded: 2026-06-06
---

Takari Smart Builder replaces Maven's default multi-module scheduler with a dependency-critical-path-aware scheduler. Instead of building level by level, it prioritizes the longest dependency chains first so worker threads stay busy throughout the build. This significantly improves throughput in larger multi-module projects, especially when combined with Maven parallel builds.

## Code Example

Create `.mvn/extensions.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<extensions>
    <extension>
        <groupId>io.takari.maven</groupId>
        <artifactId>takari-smart-builder</artifactId>
        <version>1.1.0</version>
    </extension>
</extensions>
```

Run Maven with Smart Builder and one thread per core:

```bash
mvn --builder smart -T 1C verify
```
