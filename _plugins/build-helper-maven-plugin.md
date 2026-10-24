---
title: Build Helper Maven Plugin
description: Add generated sources and resources and support other common Maven build tasks in CI.
order: 12
slug: build-helper-maven-plugin
name: Build Helper Maven Plugin
category: Source Code Policy
type: Maven Plugin
mavenCoordinates: org.codehaus.mojo:build-helper-maven-plugin
lastRelease: "3.6.2"
learnMoreText: Build Helper Maven Plugin Documentation
learnMoreHref: https://www.mojohaus.org/build-helper-maven-plugin/
tags:
  - Source Code Policy
  - Source Structure
  - Build
  - Code Generation
  - Source Directories
  - Versioning
dateAdded: 2026-10-24
---

The Build Helper Maven Plugin provides goals that support common tasks in the Maven build process. Its `add-source` and `add-test-source` goals register generated source directories, while `add-resource` and `attach-artifact` add other build outputs. The `parse-version` goal exposes version components for CI conditions, and `reserve-network-port` can allocate ports for integration tests. It is especially useful when code generators such as OpenAPI or Protobuf write sources outside Maven's default directories.

## Code Example

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>build-helper-maven-plugin</artifactId>
    <version>3.6.2</version>
    <executions>
        <execution>
            <id>add-generated-sources</id>
            <phase>generate-sources</phase>
            <goals>
                <goal>add-source</goal>
            </goals>
            <configuration>
                <sources>
                    <source>${project.build.directory}/generated-sources/openapi</source>
                </sources>
            </configuration>
        </execution>
    </executions>
</plugin>
```
