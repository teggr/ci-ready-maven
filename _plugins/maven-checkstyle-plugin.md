---
title: Maven Checkstyle Plugin
description: Enforce Java coding standards with Checkstyle in Maven CI builds.
order: 3
slug: maven-checkstyle-plugin
name: Maven Checkstyle Plugin
category: Static Analysis
type: Maven Plugin
mavenCoordinates: org.apache.maven.plugins:maven-checkstyle-plugin
lastRelease: "3.6.0"
learnMoreText: Maven Checkstyle Plugin Documentation
learnMoreHref: https://maven.apache.org/plugins/maven-checkstyle-plugin/
tags:
  - Static Analysis
  - Code Style
  - Quality Gate
dateAdded: 2026-10-24
---

The Maven Checkstyle Plugin integrates Checkstyle into Maven projects to enforce coding standards in CI. Its configurable rulesets can check naming conventions, Javadoc, whitespace, and import ordering. The plugin can generate reports for review and run a check during the `verify` phase to fail the build when violations exceed the allowed threshold. It pairs well with SpotBugs and PMD as part of a broader static analysis quality gate.

## Code Example

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.6.0</version>
    <configuration>
        <configLocation>google_checks.xml</configLocation>
        <consoleOutput>true</consoleOutput>
        <failOnViolation>true</failOnViolation>
        <maxAllowedViolations>0</maxAllowedViolations>
    </configuration>
    <executions>
        <execution>
            <id>checkstyle</id>
            <phase>verify</phase>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
