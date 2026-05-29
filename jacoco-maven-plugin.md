---
name: JaCoCo Maven Plugin
category: Coverage
type: Maven Plugin
mavenCoordinates: org.jacoco:jacoco-maven-plugin
lastRelease: "0.8.13"
learnMoreText: JaCoCo Maven Plugin Documentation
learnMoreHref: https://www.jacoco.org/jacoco/trunk/doc/maven.html
tags:
    - Coverage
dateAdded: 2026-05-29
---

JaCoCo (Java Code Coverage) is the de facto standard for measuring test coverage in Java projects. It instruments bytecode at runtime to track which lines, branches, and instructions are executed during tests. The JaCoCo Maven Plugin integrates coverage collection into the Maven lifecycle and can enforce minimum coverage thresholds, failing the build if coverage drops below the configured limits. Coverage reports are generated in HTML, XML, and CSV formats, making them easy to integrate with CI dashboards and code quality platforms.

## Code Example

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.13</version>
    <executions>
        <execution>
            <id>prepare-agent</id>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
        <execution>
            <id>check</id>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>BUNDLE</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.80</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```
