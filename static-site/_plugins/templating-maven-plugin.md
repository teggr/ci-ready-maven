---
title: Templating Maven Plugin
description: Filter Java source templates with Maven properties during the build.
order: 12
slug: templating-maven-plugin
name: Templating Maven Plugin
category: Build
type: Maven Plugin (MojoHaus)
mavenCoordinates: org.codehaus.mojo:templating-maven-plugin
lastRelease: "3.1.1"
learnMoreText: Templating Maven Plugin Documentation
learnMoreHref: https://www.mojohaus.org/templating-maven-plugin/
tags:
  - Build
  - Code Generation
  - Version Embedding
  - Templates
dateAdded: 2026-10-24
---

The Templating Maven Plugin filters Java source files in a dedicated template directory, replacing expressions such as `${project.version}` with resolved Maven properties. Its `filter-sources` goal copies filtered files from `src/main/java-templates` into generated sources during the `generate-sources` phase. This makes it a straightforward way to generate classes such as `Version.java` with build metadata while keeping generated files out of the regular source tree. It complements Maven resource filtering, which is intended for resources rather than Java source files.

## Code Example

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>templating-maven-plugin</artifactId>
    <version>3.1.1</version>
    <executions>
        <execution>
            <goals>
                <goal>filter-sources</goal>
            </goals>
            <configuration>
                <sourceDirectory>${project.basedir}/src/main/java-templates</sourceDirectory>
                <outputDirectory>${project.build.directory}/generated-sources/java-templates</outputDirectory>
            </configuration>
        </execution>
    </executions>
</plugin>
```
