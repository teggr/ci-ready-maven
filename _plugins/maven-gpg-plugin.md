---
title: Maven GPG Plugin
description: Sign Maven artifacts and POMs for secure publication.
order: 12
slug: maven-gpg-plugin
name: Maven GPG Plugin
category: Security and Compliance
type: Maven Plugin
mavenCoordinates: org.apache.maven.plugins:maven-gpg-plugin
lastRelease: "3.2.8"
learnMoreText: Maven GPG Plugin Documentation
learnMoreHref: https://maven.apache.org/plugins/maven-gpg-plugin/
tags:
  - Security and Compliance
  - Artifact Signing
  - CI
  - Release
dateAdded: 2026-10-24
---

The Apache Maven GPG Plugin creates GPG signatures for project artifacts and their POM files, which are required for Maven Central publication and let consumers verify artifact integrity. Bind its `sign` goal to the `verify` phase so artifacts are signed before deployment. For unattended CI, the BC signer reads a transferable secret key from `MAVEN_GPG_KEY` and its passphrase from `MAVEN_GPG_PASSPHRASE`; supply both through CI secrets rather than storing them in the POM.

## Code Example

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-gpg-plugin</artifactId>
    <version>3.2.8</version>
    <executions>
        <execution>
            <id>sign-artifacts</id>
            <phase>verify</phase>
            <goals>
                <goal>sign</goal>
            </goals>
            <configuration>
                <signer>bc</signer>
                <bestPractices>true</bestPractices>
                <useAgent>false</useAgent>
            </configuration>
        </execution>
    </executions>
</plugin>
```
