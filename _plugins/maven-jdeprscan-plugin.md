---
title: Maven JDeprScan Plugin
description: Detect deprecated JDK API usage in compiled classes.
order: 12
slug: maven-jdeprscan-plugin
name: Maven JDeprScan Plugin
category: Static Analysis
type: Maven Plugin
mavenCoordinates: org.apache.maven.plugins:maven-jdeprscan-plugin
lastRelease: "3.0.0"
learnMoreText: Maven JDeprScan Plugin Documentation
learnMoreHref: https://maven.apache.org/plugins/maven-jdeprscan-plugin/
tags:
  - Static Analysis
  - Deprecated APIs
  - Java
dateAdded: 2026-10-24
---

The Maven JDeprScan Plugin runs the JDK `jdeprscan` tool to detect deprecated JDK API usage in compiled classes. It helps teams identify APIs that may be removed in future Java releases and maintain compatibility across Java versions. Configure it as a CI quality gate to fail the build when deprecated API usage is found. The plugin is part of the official Apache Maven project.

## Code Example

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-jdeprscan-plugin</artifactId>
    <version>3.0.0</version>
    <executions>
        <execution>
            <id>check-deprecated-apis</id>
            <phase>verify</phase>
            <goals>
                <goal>jdeprscan</goal>
            </goals>
            <configuration>
                <failOnWarning>true</failOnWarning>
            </configuration>
        </execution>
    </executions>
</plugin>
```
