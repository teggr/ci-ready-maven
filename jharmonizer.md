---
name: JHarmonizer
category: Source Code Policy
type: Maven Plugin / CLI Tool
mavenCoordinates: io.github.lemon-ant:jharmonizer-maven-plugin
lastRelease: "0.1.0"
learnMoreText: JHarmonizer on GitHub
learnMoreHref: https://github.com/lemon-ant/JHarmonizer
tags:
    - Source Code Policy
    - Source Structure
dateAdded: 2026-05-29
---

JHarmonizer is an open-source Java source harmonization tool that makes Java class member ordering reproducible and enforceable from Maven and CI. It fills the gap between formatters (which handle text layout) and structural consistency (which governs where constants, fields, constructors, methods, and nested types appear inside a class). JHarmonizer respects declaration-order dependencies—for example, ensuring a constant used in another constant's initializer is always declared first—making the reordering safe for compilation. It supports different ordering strategies for interfaces, DTOs, test classes, utility classes, and regular production classes. It can format the reordered result with Palantir Java Format and run in either auto-fix or check-only mode.

## Code Example

```xml
<plugin>
    <groupId>io.github.lemon-ant</groupId>
    <artifactId>jharmonizer-maven-plugin</artifactId>
    <version>0.1.0</version>
    <executions>
        <execution>
            <id>harmonize-check</id>
            <goals>
                <!-- Use 'harmonize' goal to auto-fix, 'check' goal for CI -->
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
