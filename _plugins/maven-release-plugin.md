---
title: Maven Release Plugin
description: Automate tagged Maven releases and version updates.
order: 12
slug: maven-release-plugin
name: Maven Release Plugin
category: CI
type: Maven Plugin (Apache)
mavenCoordinates: org.apache.maven.plugins:maven-release-plugin
lastRelease: "3.3.1"
learnMoreText: Maven Release Plugin Documentation
learnMoreHref: https://maven.apache.org/plugins/maven-release-plugin/
tags:
  - CI
  - Release Automation
  - Version Management
  - SCM
dateAdded: 2026-10-24
---

The Maven Release Plugin automates preparing and performing a project release by removing SNAPSHOT versions, creating an SCM tag, building the release, and updating the POMs for the next development iteration. In CI/CD, `mvn -B release:prepare release:perform` creates repeatable, tagged releases without manual version editing. The plugin requires SCM configuration in the POM and is often combined with the Maven GPG Plugin to sign release artifacts.

## Code Example

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-release-plugin</artifactId>
    <version>3.3.1</version>
</plugin>
```
