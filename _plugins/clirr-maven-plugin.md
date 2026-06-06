---
title: Clirr Maven Plugin
description: Detect binary and source compatibility breaks between releases.
order: 12
slug: clirr-maven-plugin
name: Clirr Maven Plugin
category: Quality
type: Maven Plugin
mavenCoordinates: org.codehaus.mojo:clirr-maven-plugin
lastRelease: "2.8"
learnMoreText: Clirr Maven Plugin Documentation
learnMoreHref: https://www.mojohaus.org/clirr-maven-plugin/
tags:
  - Quality
  - API Compatibility
  - Semantic Versioning
  - Library Development
dateAdded: 2026-06-06
---

The Clirr Maven Plugin compares your current build against a previous released version and reports binary/source compatibility changes. It is especially useful for libraries and SDKs that need to enforce semantic versioning rules in CI. Typical breaking changes include removed methods, changed method signatures, and incompatible return type changes. Configure severity thresholds and fail settings so compatibility violations block merges before release.

## Code Example

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>clirr-maven-plugin</artifactId>
    <version>2.8</version>
    <configuration>
        <comparisonVersion>[1.4.0]</comparisonVersion>
        <minSeverity>error</minSeverity>
        <failOnError>true</failOnError>
    </configuration>
    <executions>
        <execution>
            <id>check-binary-compatibility</id>
            <phase>verify</phase>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
