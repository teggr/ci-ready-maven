---
title: Maven Shade Plugin
description: Package Maven projects and their dependencies into a single executable JAR.
order: 12
slug: maven-shade-plugin
name: Maven Shade Plugin
category: Build / Packaging
type: Maven Plugin (Apache)
mavenCoordinates: org.apache.maven.plugins:maven-shade-plugin
lastRelease: "3.6.2"
learnMoreText: Maven Shade Plugin Documentation
learnMoreHref: https://maven.apache.org/plugins/maven-shade-plugin/
tags:
  - Build
  - Packaging
  - Uber-JAR
  - Fat JAR
dateAdded: 2026-10-24
---

The Maven Shade Plugin packages a project and its dependencies into a single uber-JAR, making it useful for self-contained services and command-line tools in CI. It can relocate dependency packages to avoid classpath conflicts and merge resources such as `META-INF/services`. Its manifest transformer can set the executable JAR's main class. It is an alternative to `spring-boot-maven-plugin` for applications that do not use Spring Boot.

## Code Example

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.6.2</version>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
            <configuration>
                <relocations>
                    <relocation>
                        <pattern>com.google.common</pattern>
                        <shadedPattern>internal.com.google.common</shadedPattern>
                    </relocation>
                </relocations>
                <transformers>
                    <transformer implementation="org.apache.maven.plugins.shade.resource.ServicesResourceTransformer"/>
                    <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                        <mainClass>com.example.Main</mainClass>
                    </transformer>
                </transformers>
            </configuration>
        </execution>
    </executions>
</plugin>
```
