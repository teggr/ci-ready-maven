---
title: Maven Changelog Plugin
description: Generate SCM-based changelog and activity reports during Maven site generation.
order: 12
slug: maven-changelog-plugin
name: Maven Changelog Plugin
category: Git / Reporting
type: Maven Plugin (Apache)
mavenCoordinates: org.apache.maven.plugins:maven-changelog-plugin
lastRelease: "3.0.0-M2"
learnMoreText: Maven Changelog Plugin Documentation
learnMoreHref: https://maven.apache.org/plugins/maven-changelog-plugin/
tags:
  - Git
  - Changelog
  - Reporting
  - SCM
dateAdded: 2026-06-06
---

The Maven Changelog Plugin generates reports from your project's SCM history to help teams understand recent development activity. It can produce a changelog of recent commits, a developer activity summary, and a file activity report showing which files change most often. This plugin is typically configured as a reporting plugin for Maven Site generation and requires SCM connection details in your POM.

## Code Example

```xml
<scm>
    <connection>scm:git:https://github.com/example-org/example-repo.git</connection>
    <developerConnection>scm:git:ssh://git@github.com:example-org/example-repo.git</developerConnection>
    <url>https://github.com/example-org/example-repo</url>
</scm>

<reporting>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-changelog-plugin</artifactId>
            <version>3.0.0-M2</version>
            <reportSets>
                <reportSet>
                    <reports>
                        <report>changelog</report>
                        <report>dev-activity</report>
                        <report>file-activity</report>
                    </reports>
                </reportSet>
            </reportSets>
        </plugin>
    </plugins>
</reporting>
```
