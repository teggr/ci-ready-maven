---
title: Maven JarSigner Plugin
description: Sign and verify JAR artifacts with Java keystores in CI pipelines.
order: 12
slug: maven-jarsigner-plugin
name: Maven JarSigner Plugin
category: Security and Compliance
type: Maven Plugin
mavenCoordinates: org.apache.maven.plugins:maven-jarsigner-plugin
lastRelease: "3.1.0"
learnMoreText: Maven JarSigner Plugin Documentation
learnMoreHref: https://maven.apache.org/plugins/maven-jarsigner-plugin/
tags:
  - Security and Compliance
  - Artifact Signing
  - CI
dateAdded: 2026-06-06
---

The Maven JarSigner Plugin signs or verifies project JAR artifacts using the JDK `jarsigner` tool and a Java keystore (`.jks`). This is useful for deployment scenarios that require bytecode-level signing, such as Java Web Start and controlled enterprise runtime environments. In CI, keystore paths and credentials are typically injected through secret-backed environment variables. Unlike `maven-gpg-plugin`, which signs Maven publication metadata, this plugin signs the JAR itself.

## Code Example

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-jarsigner-plugin</artifactId>
    <version>3.1.0</version>
    <executions>
        <execution>
            <id>sign-artifacts</id>
            <phase>verify</phase>
            <goals>
                <goal>sign</goal>
            </goals>
            <configuration>
                <keystore>${env.JARSIGNER_KEYSTORE}</keystore>
                <alias>${env.JARSIGNER_ALIAS}</alias>
                <storepass>${env.JARSIGNER_STOREPASS}</storepass>
                <keypass>${env.JARSIGNER_KEYPASS}</keypass>
                <removeExistingSignatures>true</removeExistingSignatures>
            </configuration>
        </execution>
        <execution>
            <id>verify-signatures</id>
            <phase>verify</phase>
            <goals>
                <goal>verify</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
