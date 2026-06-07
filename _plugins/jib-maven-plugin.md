---
title: Jib Maven Plugin
description: Build optimized OCI container images directly from Maven without a Docker daemon.
order: 12
slug: jib-maven-plugin
name: Jib Maven Plugin
category: Docker
type: Maven Plugin (Google)
mavenCoordinates: com.google.cloud.tools:jib-maven-plugin
lastRelease: "3.5.1"
learnMoreText: Jib Maven Plugin Documentation
learnMoreHref: https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin
tags:
  - Docker
  - Container
  - OCI
  - CI
  - Build
dateAdded: 2026-06-06
---

Jib is a container image build plugin for Java that creates optimized and reproducible OCI images directly from Maven, without requiring a local Docker daemon or a Dockerfile. It splits dependencies, resources, and classes into distinct layers to maximize cache reuse and speed up incremental CI builds. Jib can push images directly to registries such as Docker Hub, GCR, ECR, and other OCI-compatible registries, and is a modern default for Maven-based Java containerization pipelines.

## Code Example

```xml
<plugin>
    <groupId>com.google.cloud.tools</groupId>
    <artifactId>jib-maven-plugin</artifactId>
    <version>3.5.1</version>
    <configuration>
        <from>
            <image>gcr.io/distroless/java17-debian12</image>
        </from>
        <to>
            <image>registry.example.com/acme/my-app:${project.version}</image>
        </to>
        <container>
            <creationTime>USE_CURRENT_TIMESTAMP</creationTime>
        </container>
    </configuration>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>build</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
