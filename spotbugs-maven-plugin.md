---
name: SpotBugs Maven Plugin
category: Static Analysis
type: Maven Plugin
mavenCoordinates: com.github.spotbugs:spotbugs-maven-plugin
lastRelease: "4.9.3.0"
learnMoreText: SpotBugs Maven Plugin Documentation
learnMoreHref: https://spotbugs.github.io/spotbugs-maven-plugin/
tags:
    - Static Analysis
dateAdded: 2026-05-29
---

SpotBugs is the spiritual successor to FindBugs and detects bug patterns in Java bytecode. It analyzes compiled class files for over 400 known bug patterns including null pointer dereferences, infinite recursive loops, bad uses of the Java library, deadlocks, and other concurrency problems. The SpotBugs Maven Plugin integrates SpotBugs into the Maven build lifecycle, allowing you to fail the build when bugs are found and generate detailed bug reports as part of your CI pipeline quality gate.

## Code Example

```xml
<plugin>
    <groupId>com.github.spotbugs</groupId>
    <artifactId>spotbugs-maven-plugin</artifactId>
    <version>4.9.3.0</version>
    <configuration>
        <effort>Max</effort>
        <threshold>Low</threshold>
        <failOnError>true</failOnError>
        <includeFilterFile>spotbugs-include.xml</includeFilterFile>
        <excludeFilterFile>spotbugs-exclude.xml</excludeFilterFile>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
