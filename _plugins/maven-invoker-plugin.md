---
title: Maven Invoker Plugin
description: Run isolated Maven integration-test projects to verify plugin behavior in CI.
order: 12
slug: maven-invoker-plugin
name: Maven Invoker Plugin
category: CI / Build
type: Maven Plugin (Apache)
mavenCoordinates: org.apache.maven.plugins:maven-invoker-plugin
lastRelease: "3.10.1"
learnMoreText: Maven Invoker Plugin Documentation
learnMoreHref: https://maven.apache.org/plugins/maven-invoker-plugin/
tags:
  - CI
  - Integration Testing
  - Plugin Development
dateAdded: 2026-10-24
---

The Maven Invoker Plugin runs Maven projects from a directory such as `src/it` as isolated builds, making it an essential tool for testing Maven plugins across configurations and Maven versions. It can stage the current project and reactor dependencies in a test repository before running each integration test. Optional post-build scripts can verify that each build produced the expected results. The same approach can also be used for acceptance testing in multi-module projects.

## Code Example

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-invoker-plugin</artifactId>
    <version>3.10.1</version>
    <configuration>
        <cloneProjectsTo>${project.build.directory}/it</cloneProjectsTo>
        <localRepositoryPath>${project.build.directory}/local-repo</localRepositoryPath>
        <postBuildHookScript>verify</postBuildHookScript>
    </configuration>
    <executions>
        <execution>
            <id>integration-test</id>
            <goals>
                <goal>install</goal>
                <goal>run</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
