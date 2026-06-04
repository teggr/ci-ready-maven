---
title: OWASP Dependency-Check
description: Detect vulnerable dependencies and fail CI builds on CVEs.
order: 3
slug: owasp-dependency-check
name: OWASP Dependency-Check
category: Security and Compliance
type: Maven Plugin
mavenCoordinates: org.owasp:dependency-check-maven
lastRelease: "12.1.0"
learnMoreText: OWASP Dependency-Check Documentation
learnMoreHref: https://dependency-check.github.io/DependencyCheck/dependency-check-maven/
tags:
  - Security and Compliance
  - Dependency Governance
dateAdded: 2026-05-29
---

OWASP Dependency-Check is a software composition analysis tool that identifies project dependencies with known vulnerabilities by matching them against vulnerability databases such as the NVD. The Maven plugin integrates this check directly into the build lifecycle so teams can fail CI pipelines when vulnerable libraries are detected. It supports configurable CVSS thresholds, suppression files, and multiple report formats for governance and audit requirements.

## Code Example

```xml
<plugin>
    <groupId>org.owasp</groupId>
    <artifactId>dependency-check-maven</artifactId>
    <version>12.1.0</version>
    <configuration>
        <failBuildOnCVSS>7</failBuildOnCVSS>
        <format>HTML</format>
        <suppressionFiles>
            <suppressionFile>dependency-check-suppressions.xml</suppressionFile>
        </suppressionFiles>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
