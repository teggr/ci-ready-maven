---
title: Maven Assembly Plugin
description: Package project files and dependencies into distribution archives in Maven builds.
order: 12
slug: maven-assembly-plugin
name: Maven Assembly Plugin
category: Build / Packaging
type: Maven Plugin (Apache)
mavenCoordinates: org.apache.maven.plugins:maven-assembly-plugin
lastRelease: "3.8.0"
learnMoreText: Maven Assembly Plugin Documentation
learnMoreHref: https://maven.apache.org/plugins/maven-assembly-plugin/
tags:
  - Build
  - Packaging
  - Distribution
  - Archive
dateAdded: 2026-10-24
---

The Maven Assembly Plugin creates distribution archives such as ZIP and TAR files from project sources, binaries, and dependencies. Assembly descriptors define which files are included, allowing teams to package runnable artifacts alongside configuration and scripts. Built-in descriptors include `jar-with-dependencies`, `bin`, `src`, and `project`, while custom XML descriptors provide fine-grained control. It is useful for producing complete deployment packages rather than only a fat JAR.

## Code Example

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-assembly-plugin</artifactId>
    <version>3.8.0</version>
    <executions>
        <execution>
            <id>make-distribution</id>
            <phase>package</phase>
            <goals>
                <goal>single</goal>
            </goals>
            <configuration>
                <descriptorRefs>
                    <descriptorRef>bin</descriptorRef>
                </descriptorRefs>
            </configuration>
        </execution>
    </executions>
</plugin>
```
