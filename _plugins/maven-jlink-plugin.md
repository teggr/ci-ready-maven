---
title: Maven JLink Plugin
description: Build custom Java runtime images containing only the modules needed to run your application.
slug: maven-jlink-plugin
name: Maven JLink Plugin
category: Build / Packaging
type: Maven Plugin (Apache)
mavenCoordinates: org.apache.maven.plugins:maven-jlink-plugin
lastRelease: "3.3.0"
learnMoreText: Maven JLink Plugin Documentation
learnMoreHref: https://maven.apache.org/plugins/maven-jlink-plugin/
tags:
  - Build
  - Packaging
  - JLink
  - Java Modules
  - Container
dateAdded: 2026-10-24
---

The Maven JLink Plugin uses the JDK's `jlink` tool to create custom Java runtime images from modular applications and their dependencies. These self-contained distributions include only the Java modules needed by the application, reducing deployment size and avoiding the need for a full JDK on the target system. This makes the plugin useful for containerized and cloud-native deployments. Version 3.3.0 requires JDK 11 or newer.

## Code Example

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-jlink-plugin</artifactId>
    <version>3.3.0</version>
    <extensions>true</extensions>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>jlink</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
