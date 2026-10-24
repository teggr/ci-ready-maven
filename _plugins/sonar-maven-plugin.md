---
title: SonarScanner for Maven
description: Upload Maven project analysis to SonarQube or SonarCloud in CI.
order: 12
slug: sonar-maven-plugin
name: SonarScanner for Maven
category: Static Analysis
type: Maven Plugin
mavenCoordinates: org.sonarsource.scanner.maven:sonar-maven-plugin
lastRelease: "5.8.0.7211"
learnMoreText: SonarScanner for Maven Documentation
learnMoreHref: https://docs.sonarqube.org/latest/analyzing-source-code/scanners/sonarscanner-for-maven/
tags:
  - Static Analysis
  - CI
  - Quality Gate
  - Security
dateAdded: 2026-10-24
---

SonarScanner for Maven is the recommended scanner for Maven projects analyzed with SonarQube or SonarCloud, using project metadata directly from the POM to minimize manual configuration. It uploads analysis results for bugs, vulnerabilities, code smells, duplication, and coverage to a SonarQube server or SonarCloud account, where quality gates can enforce project standards. It can import coverage data produced by JaCoCo. Run it after `mvn verify` in CI with `mvn verify sonar:sonar -Dsonar.token=$TOKEN`; a SonarQube server or SonarCloud account is required.

## Code Example

```xml
<plugin>
    <groupId>org.sonarsource.scanner.maven</groupId>
    <artifactId>sonar-maven-plugin</artifactId>
    <version>5.8.0.7211</version>
</plugin>
```
