---
title: Coveralls Maven Plugin
description: Submit Java coverage reports to Coveralls from CI builds.
order: 12
slug: coveralls-maven-plugin
name: Coveralls Maven Plugin
category: Coverage
type: Maven Plugin
mavenCoordinates: com.github.hazendaz.maven:coveralls-maven-plugin
lastRelease: "5.1.0"
learnMoreText: Coveralls Maven Plugin Documentation
learnMoreHref: https://github.com/hazendaz/coveralls-maven-plugin
tags:
  - Coverage
dateAdded: 2026-10-24
---

The Coveralls Maven Plugin submits Java code coverage reports to Coveralls from CI builds. It supports JaCoCo and Cobertura reports, aggregates coverage in multi-module projects, and recognizes common CI services including Travis CI, CircleCI, GitHub Actions, Jenkins, and Bamboo. Run it after generating coverage reports to send results to Coveralls. The maintained fork also supports other coverage tools and CI services.

## Code Example

```xml
<plugin>
    <groupId>com.github.hazendaz.maven</groupId>
    <artifactId>coveralls-maven-plugin</artifactId>
    <version>5.1.0</version>
</plugin>
```
