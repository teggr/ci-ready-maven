---
title: Spotify dockerfile-maven-plugin
description: Dockerfile-first image builds and pushes integrated into Maven lifecycle.
order: 12
slug: spotify-dockerfile-maven-plugin
name: Spotify dockerfile-maven-plugin
category: Docker
type: Maven Plugin (Spotify)
mavenCoordinates: com.spotify:dockerfile-maven-plugin
lastRelease: "1.4.13"
learnMoreText: Spotify dockerfile-maven-plugin on GitHub
learnMoreHref: https://github.com/spotify/dockerfile-maven
tags:
  - Docker
  - Container
  - CI
  - Build
dateAdded: 2026-06-06
---

Spotify dockerfile-maven-plugin integrates Maven builds with standard Dockerfiles. It follows a Dockerfile-first approach where image configuration stays in the `Dockerfile` and Maven provides build artefacts. Bound to lifecycle phases, `mvn package` can build the image while `mvn deploy` can push it. The plugin also integrates with the Maven reactor for multi-module image dependency workflows.

Status: Mature / No new features. For new projects, Jib is generally recommended.

## Code Example

```xml
<plugin>
    <groupId>com.spotify</groupId>
    <artifactId>dockerfile-maven-plugin</artifactId>
    <version>1.4.13</version>
    <executions>
        <execution>
            <id>build-image</id>
            <phase>package</phase>
            <goals>
                <goal>build</goal>
            </goals>
        </execution>
        <execution>
            <id>push-image</id>
            <phase>deploy</phase>
            <goals>
                <goal>push</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <repository>ghcr.io/your-org/${project.artifactId}</repository>
        <tag>${project.version}</tag>
    </configuration>
</plugin>
```
