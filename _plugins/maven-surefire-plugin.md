---
title: Maven Surefire Plugin
description: Run unit tests during the Maven test phase and fail CI builds when tests fail.
order: 12
slug: maven-surefire-plugin
name: Maven Surefire Plugin
category: Test Tools
type: Maven Plugin (Apache)
mavenCoordinates: org.apache.maven.plugins:maven-surefire-plugin
lastRelease: "3.6.0"
learnMoreText: Maven Surefire Plugin Documentation
learnMoreHref: https://maven.apache.org/surefire/maven-surefire-plugin/
tags:
  - Test
  - JUnit
  - TestNG
  - Unit Testing
dateAdded: 2026-10-24
---

The Maven Surefire Plugin runs unit tests during the `test` phase of the Maven build lifecycle and fails the build when tests fail, making it a foundational CI quality gate. It supports JUnit 4, JUnit 5, and TestNG, and generates plain-text and XML test reports. Its configuration includes parallel test execution, test forking, test inclusion and exclusion, rerunning failing tests, and control over the test classpath.

## Code Example

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.6.0</version>
</plugin>
```
