---
title: Maven Compiler Plugin
description: Compile Java sources and enforce a consistent Java release in Maven builds.
order: 12
slug: maven-compiler-plugin
name: Maven Compiler Plugin
category: Build
type: Maven Plugin
mavenCoordinates: org.apache.maven.plugins:maven-compiler-plugin
lastRelease: "3.16.0"
learnMoreText: Maven Compiler Plugin Documentation
learnMoreHref: https://maven.apache.org/plugins/maven-compiler-plugin/
tags:
  - Build
  - Compilation
  - Java
  - Core
dateAdded: 2026-10-24
---

The Maven Compiler Plugin is Maven's core plugin for compiling Java source files during the `compile` and `test-compile` phases. Setting the `release` option makes the Java language, API, and bytecode target explicit, helping CI builds remain consistent across JDK versions. The plugin also supports annotation processor paths, compiler arguments, and incremental compilation. Its 4.x beta line adds improvements for Java 9+ modules.

## Code Example

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.16.0</version>
    <configuration>
        <release>21</release>
    </configuration>
</plugin>
```
