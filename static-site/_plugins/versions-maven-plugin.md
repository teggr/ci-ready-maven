---
title: Versions Maven Plugin
description: Inspect and manage dependency, plugin, and project versions in Maven builds.
order: 12
slug: versions-maven-plugin
name: Versions Maven Plugin
category: Dependency Governance
type: Maven Plugin
mavenCoordinates: org.codehaus.mojo:versions-maven-plugin
lastRelease: "2.23.0"
learnMoreText: Versions Maven Plugin Documentation
learnMoreHref: https://www.mojohaus.org/versions-maven-plugin/
tags:
  - Dependency Governance
  - Version Management
dateAdded: 2026-10-24
---

The Versions Maven Plugin provides goals to inspect and manage dependency, build plugin, parent, and project versions in Maven POMs. Its `versions:display-dependency-updates` goal reports available dependency updates, making it useful in CI workflows that automate dependency update pull requests with tools such as Dependabot or Renovate. Other goals can update POM versions and should be run in controlled automation. To fail a build when SNAPSHOT dependencies are present, use the Maven Enforcer Plugin's `requireReleaseDeps` rule.

## Code Example

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>versions-maven-plugin</artifactId>
    <version>2.23.0</version>
    <executions>
        <execution>
            <goals>
                <goal>display-dependency-updates</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
