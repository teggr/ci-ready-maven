---
title: Maven Deploy Plugin
description: Publish Maven build artifacts to remote repositories during the deploy phase.
order: 12
slug: maven-deploy-plugin
name: Maven Deploy Plugin
category: CI / Build
type: Maven Plugin (Apache)
mavenCoordinates: org.apache.maven.plugins:maven-deploy-plugin
lastRelease: "3.2.0"
learnMoreText: Maven Deploy Plugin Documentation
learnMoreHref: https://maven.apache.org/plugins/maven-deploy-plugin/
tags:
  - CI
  - Deploy
  - Artifact Repository
dateAdded: 2026-10-24
---

The Maven Deploy Plugin publishes artifacts produced by a Maven build to a remote repository. Maven invokes its `deploy` goal during the `deploy` lifecycle phase, after the build and verification phases complete successfully. It deploys project artifacts and POMs, with optional attachments such as source and Javadoc archives. Configure the target repository in `distributionManagement` and provide credentials through Maven `settings.xml` for use with repository managers such as Nexus, Artifactory, or Maven Central.

## Code Example

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-deploy-plugin</artifactId>
    <version>3.2.0</version>
</plugin>
```
