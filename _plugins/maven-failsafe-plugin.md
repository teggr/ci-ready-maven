---
title: Maven Failsafe Plugin
description: Run integration tests during Maven builds and defer failures until verification.
order: 12
slug: maven-failsafe-plugin
name: Maven Failsafe Plugin
category: Test Tools
type: Maven Plugin (Apache)
mavenCoordinates: org.apache.maven.plugins:maven-failsafe-plugin
lastRelease: "3.6.0"
learnMoreText: Maven Failsafe Plugin Documentation
learnMoreHref: https://maven.apache.org/surefire/maven-failsafe-plugin/
tags:
  - Test
  - Integration Testing
  - CI
dateAdded: 2026-10-24
---

The Maven Failsafe Plugin runs integration tests as part of the Maven lifecycle and is commonly paired with the Surefire Plugin for unit tests. By convention, it discovers test classes such as `*IT.java`. Unlike a test goal that fails the build immediately, Failsafe records integration-test failures and reports them in the `verify` phase, allowing cleanup goals to run first. This makes it useful for CI pipelines that start and stop services or containers around integration tests.

## Code Example

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <version>3.6.0</version>
    <executions>
        <execution>
            <goals>
                <goal>integration-test</goal>
                <goal>verify</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
