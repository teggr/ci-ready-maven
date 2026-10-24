---
title: Buildnumber Maven Plugin
description: Add SCM revision-based build numbers to Maven projects for CI traceability.
order: 12
slug: buildnumber-maven-plugin
name: Buildnumber Maven Plugin
category: Git / CI
type: Maven Plugin (MojoHaus)
mavenCoordinates: org.codehaus.mojo:buildnumber-maven-plugin
lastRelease: "3.3.1"
learnMoreText: Buildnumber Maven Plugin Documentation
learnMoreHref: https://www.mojohaus.org/buildnumber-maven-plugin/
tags:
  - CI
  - Git
  - Build Number
  - Version Traceability
dateAdded: 2026-10-24
---

The Buildnumber Maven Plugin generates a build number from the current source control revision, making artifacts easier to trace back to the source that produced them. It supports SCM providers such as Git, Subversion, and Mercurial, and exposes the result as the `${buildNumber}` Maven property. The value can be included in artifact manifests, application properties, or version strings. For builds that do not use an SCM revision, the plugin can also generate timestamp-based build numbers.

## Code Example

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>buildnumber-maven-plugin</artifactId>
    <version>3.3.1</version>
    <executions>
        <execution>
            <id>create-build-number</id>
            <phase>validate</phase>
            <goals>
                <goal>create</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
