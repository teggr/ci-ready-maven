---
title: FindBugs Maven Plugin
description: Legacy bytecode analysis for detecting potential bugs in Java programs.
order: 12
slug: findbugs-maven-plugin
name: FindBugs Maven Plugin
category: Static Analysis
type: Maven Plugin
mavenCoordinates: org.codehaus.mojo:findbugs-maven-plugin
lastRelease: "3.0.5"
learnMoreText: FindBugs Maven Plugin Documentation
learnMoreHref: https://www.mojohaus.org/findbugs-maven-plugin/
tags:
  - Static Analysis
  - Bug Detection (Legacy)
dateAdded: 2026-10-24
---

The FindBugs Maven Plugin integrates FindBugs static analysis into Maven and detects potential bugs by analyzing Java bytecode. FindBugs is no longer actively maintained, so this entry is provided for historical reference and projects using older Maven builds. SpotBugs (`com.github.spotbugs:spotbugs-maven-plugin`) is its actively maintained successor and is recommended for new projects.

## Code Example

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>findbugs-maven-plugin</artifactId>
    <version>3.0.5</version>
    <executions>
        <execution>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
