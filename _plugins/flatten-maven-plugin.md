---
title: Flatten Maven Plugin
description: Produce a consumer-ready POM for reliable Maven CI-friendly releases.
order: 12
slug: flatten-maven-plugin
name: Flatten Maven Plugin
category: CI / Build
type: Maven Plugin (MojoHaus)
mavenCoordinates: org.codehaus.mojo:flatten-maven-plugin
lastRelease: "1.8.0"
learnMoreText: Flatten Maven Plugin Documentation
learnMoreHref: https://www.mojohaus.org/flatten-maven-plugin/
tags:
  - CI
  - Build
  - POM
  - Version Management
dateAdded: 2026-10-24
---

The Flatten Maven Plugin creates a simplified POM for consumers by resolving build-time details such as parent references and CI-friendly version properties. Binding its `flatten` goal before install or deploy ensures published artifacts contain the resolved project version rather than a `${revision}` placeholder. The `resolveCiFriendliesOnly` mode is useful for projects using Maven's `${revision}`, `${sha1}`, or `${changelist}` version properties. Its `clean` goal removes the generated flattened POM during a clean build.

## Code Example

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>flatten-maven-plugin</artifactId>
    <version>1.8.0</version>
    <configuration>
        <updatePomFile>true</updatePomFile>
        <flattenMode>resolveCiFriendliesOnly</flattenMode>
    </configuration>
    <executions>
        <execution>
            <id>flatten</id>
            <phase>process-resources</phase>
            <goals>
                <goal>flatten</goal>
            </goals>
        </execution>
        <execution>
            <id>flatten.clean</id>
            <phase>clean</phase>
            <goals>
                <goal>clean</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
