---
title: Animal Sniffer Maven Plugin
description: Verify API usage compatibility against a target Java or Android signature.
order: 12
slug: animal-sniffer-maven-plugin
name: Animal Sniffer Maven Plugin
category: Static Analysis
type: Maven Plugin
mavenCoordinates: org.codehaus.mojo:animal-sniffer-maven-plugin
lastRelease: "1.26"
learnMoreText: Animal Sniffer Maven Plugin Documentation
learnMoreHref: https://www.mojohaus.org/animal-sniffer/animal-sniffer-maven-plugin/
tags:
  - Static Analysis
dateAdded: 2026-06-06
---

The Animal Sniffer Maven Plugin checks compiled class files against an API signature to ensure your code only uses APIs available in the runtime you target. This helps prevent accidental use of newer JDK methods when you build on newer toolchains but must remain compatible with older Java baselines (for example, compiling on Java 21 while enforcing Java 8 runtime compatibility). It can also be used with Android and Java EE signature artifacts for cross-platform API compatibility gates in CI.

## Code Example

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>animal-sniffer-maven-plugin</artifactId>
    <version>1.26</version>
    <executions>
        <execution>
            <id>check-java-api-compatibility</id>
            <phase>verify</phase>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <signature>
                    <groupId>org.codehaus.mojo.signature</groupId>
                    <artifactId>java18</artifactId>
                    <version>1.0</version>
                </signature>
            </configuration>
        </execution>
    </executions>
</plugin>
```
