---
name: PMD Maven Plugin
category: Static Analysis
type: Maven Plugin
mavenCoordinates: org.apache.maven.plugins:maven-pmd-plugin
lastRelease: "3.26.0"
learnMoreText: PMD Maven Plugin Documentation
learnMoreHref: https://maven.apache.org/plugins/maven-pmd-plugin/
tags:
    - Static Analysis
dateAdded: 2026-05-29
---

PMD is a static source code analyser that finds common programming flaws such as unused variables, empty catch blocks, unnecessary object creation, and more. The Maven PMD Plugin integrates PMD and CPD (Copy-Paste Detector) into the Maven build. PMD can be configured with built-in rulesets or custom rules to enforce your team's coding standards. CPD detects duplicate code across your codebase, helping to identify areas that should be refactored. Both checks can be configured to fail the build when violations exceed a defined threshold.

## Code Example

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-pmd-plugin</artifactId>
    <version>3.26.0</version>
    <configuration>
        <rulesets>
            <ruleset>/rulesets/java/maven-pmd-plugin-default.xml</ruleset>
        </rulesets>
        <failOnViolation>true</failOnViolation>
        <printFailingErrors>true</printFailingErrors>
        <minimumTokens>100</minimumTokens>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>check</goal>
                <goal>cpd-check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
