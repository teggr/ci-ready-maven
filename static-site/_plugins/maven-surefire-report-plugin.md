---
title: Maven Surefire Report Plugin
description: Generate HTML reports from Surefire and Failsafe test results.
order: 12
slug: maven-surefire-report-plugin
name: Maven Surefire Report Plugin
category: Test Tools / Reporting
type: Maven Plugin (Apache)
mavenCoordinates: org.apache.maven.plugins:maven-surefire-report-plugin
lastRelease: "3.6.0"
learnMoreText: Maven Surefire Report Plugin Documentation
learnMoreHref: https://maven.apache.org/surefire/maven-surefire-report-plugin/
tags:
  - Test
  - Reporting
  - HTML Report
dateAdded: 2026-10-24
---

The Maven Surefire Report Plugin parses XML test results produced by the Surefire or Failsafe plugins and generates a human-readable HTML report. It summarizes passing and failing tests, execution times, error messages, and stack traces. As a reporting plugin, it runs during Maven Site generation and helps publish test results to CI dashboards.

## Code Example

```xml
<reporting>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-report-plugin</artifactId>
            <version>3.6.0</version>
        </plugin>
    </plugins>
</reporting>
```
