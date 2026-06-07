---
title: Fabric8 Docker Maven Plugin
description: Build images and orchestrate containers for integration tests in Maven CI pipelines.
order: 12
slug: fabric8-docker-maven-plugin
name: Fabric8 Docker Maven Plugin
category: Docker
type: Maven Plugin (Fabric8)
mavenCoordinates: io.fabric8:docker-maven-plugin
lastRelease: "0.48.1"
learnMoreText: Fabric8 Docker Maven Plugin Documentation
learnMoreHref: https://dmp.fabric8.io/
tags:
  - Docker
  - Container
  - Integration Testing
  - CI
dateAdded: 2026-06-06
---

The Fabric8 Docker Maven Plugin manages the Docker lifecycle directly from Maven. It can build images from Dockerfiles or XML configuration, start and stop containers around integration tests, and push versioned images to registries. The plugin also supports docker-compose-like multi-container setups with links, shared volumes, wait strategies, and dynamic host port mappings passed to tests via Maven properties.

## Code Example

```xml
<plugin>
    <groupId>io.fabric8</groupId>
    <artifactId>docker-maven-plugin</artifactId>
    <version>0.48.1</version>
    <configuration>
        <images>
            <image>
                <name>postgres:16</name>
                <alias>db</alias>
                <run>
                    <env>
                        <POSTGRES_PASSWORD>test</POSTGRES_PASSWORD>
                    </env>
                    <ports>
                        <port>db.port:5432</port>
                    </ports>
                    <wait>
                        <log>database system is ready to accept connections</log>
                        <time>30000</time>
                    </wait>
                </run>
            </image>
        </images>
    </configuration>
    <executions>
        <execution>
            <id>start-containers</id>
            <phase>pre-integration-test</phase>
            <goals>
                <goal>start</goal>
            </goals>
        </execution>
        <execution>
            <id>stop-containers</id>
            <phase>post-integration-test</phase>
            <goals>
                <goal>stop</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
