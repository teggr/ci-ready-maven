---
title: Exec Maven Plugin
description: Run system commands and Java classes from a Maven build.
order: 12
slug: exec-maven-plugin
name: Exec Maven Plugin
category: CI / Build
type: Maven Plugin (MojoHaus)
mavenCoordinates: org.codehaus.mojo:exec-maven-plugin
lastRelease: "3.6.4"
learnMoreText: Exec Maven Plugin Documentation
learnMoreHref: https://www.mojohaus.org/exec-maven-plugin/
tags:
  - CI
  - Build
  - Scripting
  - Automation
dateAdded: 2026-10-24
---

The Exec Maven Plugin runs external system programs with `exec:exec` or Java main classes in the Maven JVM with `exec:java`. It is useful in CI pipelines for custom scripts, code generators, database migrations, and other tools without a dedicated Maven plugin. These goals make it a flexible glue plugin for coordinating steps in complex Maven builds.

## Code Example

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>exec-maven-plugin</artifactId>
    <version>3.6.4</version>
    <executions>
        <execution>
            <id>run-system-command</id>
            <goals>
                <goal>exec</goal>
            </goals>
            <configuration>
                <executable>mvn</executable>
                <arguments>
                    <argument>--version</argument>
                </arguments>
            </configuration>
        </execution>
        <execution>
            <id>run-java-class</id>
            <goals>
                <goal>java</goal>
            </goals>
            <configuration>
                <mainClass>com.example.Main</mainClass>
            </configuration>
        </execution>
    </executions>
</plugin>
```
