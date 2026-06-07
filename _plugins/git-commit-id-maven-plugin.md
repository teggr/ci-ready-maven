---
title: git-commit-id-maven-plugin
description: Embed Git repository metadata into your build for runtime provenance and CI traceability.
order: 12
slug: git-commit-id-maven-plugin
name: git-commit-id-maven-plugin
category: Git / CI
type: Maven Plugin
mavenCoordinates: io.github.git-commit-id:git-commit-id-maven-plugin
lastRelease: "9.0.1"
learnMoreText: git-commit-id-maven-plugin on GitHub
learnMoreHref: https://github.com/git-commit-id/git-commit-id-maven-plugin
tags:
  - Git
  - CI
  - Build Provenance
  - Version Info
dateAdded: 2026-06-06
---

The git-commit-id-maven-plugin exposes Git repository metadata as Maven properties during the build. Properties include the commit SHA (`git.commit.id`), branch name (`git.branch`), commit timestamp, nearest tag, dirty flag, and more. These values can be embedded into JAR manifests, `application.properties`, or health-check endpoints to provide build provenance at runtime. The plugin can also generate a `git.properties` file that is bundled into the artifact. It is used by Spring Boot's Actuator `/actuator/info` endpoint by default and is widely adopted across open-source and enterprise Java projects that need traceable, reproducible builds.

## Code Example

```xml
<plugin>
    <groupId>io.github.git-commit-id</groupId>
    <artifactId>git-commit-id-maven-plugin</artifactId>
    <version>9.0.1</version>
    <executions>
        <execution>
            <id>get-the-git-infos</id>
            <goals>
                <goal>revision</goal>
            </goals>
            <phase>initialize</phase>
        </execution>
    </executions>
    <configuration>
        <!-- Generate a git.properties file inside the JAR -->
        <generateGitPropertiesFile>true</generateGitPropertiesFile>
        <generateGitPropertiesFilename>
            ${project.build.outputDirectory}/git.properties
        </generateGitPropertiesFilename>
        <!-- Fail the build if no Git repository is found -->
        <failOnNoGitDirectory>true</failOnNoGitDirectory>
        <!-- Include only the most useful properties to keep the file small -->
        <includeOnlyProperties>
            <includeOnlyProperty>git.branch</includeOnlyProperty>
            <includeOnlyProperty>git.commit.id</includeOnlyProperty>
            <includeOnlyProperty>git.commit.id.abbrev</includeOnlyProperty>
            <includeOnlyProperty>git.commit.time</includeOnlyProperty>
            <includeOnlyProperty>git.dirty</includeOnlyProperty>
            <includeOnlyProperty>git.closest.tag.name</includeOnlyProperty>
        </includeOnlyProperties>
    </configuration>
</plugin>
```
