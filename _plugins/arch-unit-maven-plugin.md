---
title: ArchUnit Maven Plugin
description: Enforce Java architecture rules during the Maven test phase.
order: 12
slug: arch-unit-maven-plugin
name: ArchUnit Maven Plugin
category: Static Analysis
type: Maven Plugin
mavenCoordinates: com.societegenerale.commons:arch-unit-maven-plugin
lastRelease: "5.0.0"
learnMoreText: ArchUnit Maven Plugin Documentation
learnMoreHref: https://github.com/societe-generale/arch-unit-maven-plugin
tags:
  - Static Analysis
  - Architecture
  - Quality Gate
dateAdded: 2026-10-24
---

The ArchUnit Maven Plugin runs Java architecture rules as part of the Maven build, failing the build when a rule is violated. Teams can use its built-in rules or configure reusable Java rules written with ArchUnit's fluent API. These rules can enforce package dependencies, layering, naming conventions, and other architectural boundaries. Bind the plugin to the `test` phase to make architecture checks part of CI.

## Code Example

```xml
<plugin>
    <groupId>com.societegenerale.commons</groupId>
    <artifactId>arch-unit-maven-plugin</artifactId>
    <version>5.0.0</version>
    <configuration>
        <rules>
            <preConfiguredRules>
                <rule>com.societegenerale.commons.plugin.rules.NoPublicFieldRuleTest</rule>
            </preConfiguredRules>
        </rules>
    </configuration>
    <executions>
        <execution>
            <phase>test</phase>
            <goals>
                <goal>arch-test</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
