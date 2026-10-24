---
title: Maven Artifact Plugin
description: Verify Maven artifact reproducibility against a reference build.
order: 12
slug: maven-artifact-plugin
name: Maven Artifact Plugin
category: Dependency Governance
type: Maven Plugin
mavenCoordinates: org.apache.maven.plugins:maven-artifact-plugin
lastRelease: "3.7.0"
learnMoreText: Maven Artifact Plugin Documentation
learnMoreHref: https://maven.apache.org/plugins/maven-artifact-plugin/
tags:
  - Build Reproducibility
  - Security and Compliance
dateAdded: 2026-10-24
---

The Maven Artifact Plugin supports reproducible-build verification for Maven projects. Its `buildinfo` goal records the build environment and packaged outputs in a `.buildinfo` file, while `compare` checks build outputs against a reference repository. Since `buildinfo` is deprecated, use `compare` for CI reproducibility checks; it fails the build when differences are found.

## Code Example

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-artifact-plugin</artifactId>
    <version>3.7.0</version>
    <executions>
        <execution>
            <phase>verify</phase>
            <goals>
                <goal>compare</goal>
            </goals>
            <configuration>
                <referenceRepo>central</referenceRepo>
            </configuration>
        </execution>
    </executions>
</plugin>
```
