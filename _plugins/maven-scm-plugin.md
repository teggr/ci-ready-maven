---
title: Maven SCM Plugin
description: Automate source control operations such as tag, branch, and status from Maven.
order: 12
slug: maven-scm-plugin
name: Maven SCM Plugin
category: Git / CI
type: Maven Plugin (Apache)
mavenCoordinates: org.apache.maven.plugins:maven-scm-plugin
lastRelease: "2.2.1"
learnMoreText: Maven SCM Plugin Documentation
learnMoreHref: https://maven.apache.org/scm/maven-scm-plugin/
tags:
  - Git
  - SCM
  - CI
  - Version Control
dateAdded: 2026-06-06
---

The Maven SCM Plugin provides vendor-neutral goals for interacting with source control systems such as Git, Subversion, and Mercurial. It supports commands like `scm:checkout`, `scm:tag`, `scm:branch`, `scm:status`, `scm:diff`, and `scm:add`, and is used internally by the Maven Release Plugin during release tagging. Teams can also invoke it directly in CI workflows when they need consistent SCM actions from Maven builds.

## Code Example

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-scm-plugin</artifactId>
    <version>2.2.1</version>
    <configuration>
        <connectionUrl>scm:git:https://github.com/example/acme-service.git</connectionUrl>
        <tag>${project.artifactId}-${project.version}</tag>
    </configuration>
</plugin>
```
