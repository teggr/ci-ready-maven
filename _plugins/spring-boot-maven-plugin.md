---
title: Spring Boot Maven Plugin
description: Build OCI images with Cloud Native Buildpacks and manage Spring Boot packaging.
order: 12
slug: spring-boot-maven-plugin
name: Spring Boot Maven Plugin
category: Docker / Build
type: Maven Plugin (Spring)
mavenCoordinates: org.springframework.boot:spring-boot-maven-plugin
lastRelease: "3.5.0"
learnMoreText: Spring Boot Maven Plugin Documentation
learnMoreHref: https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/htmlsingle/#build-image
tags:
  - Docker
  - Spring Boot
  - Cloud Native Buildpacks
  - Container
  - Build
dateAdded: 2026-06-06
---

The Spring Boot Maven Plugin supports the full Spring Boot application packaging lifecycle, including executable JAR/WAR packaging, layered JAR support, and running applications directly from Maven. Its `build-image` goal uses Cloud Native Buildpacks (CNB) to create production-ready OCI images without requiring a Dockerfile. This approach automatically detects the application type, applies buildpack-managed updates, and produces optimized layered images for faster rebuilds and startup.

Note: unlike Jib, the `build-image` goal requires a running Docker daemon.

## Code Example

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <version>3.5.0</version>
    <executions>
        <execution>
            <goals>
                <goal>build-image</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <image>
            <name>${project.artifactId}:${project.version}</name>
        </image>
    </configuration>
</plugin>
```
