---
name: SortPOM Maven Plugin
category: Source Code Policy
type: Maven Plugin
mavenCoordinates: com.github.ekryd.sortpom:sortpom-maven-plugin
lastRelease: "4.0.0"
learnMoreText: SortPOM Maven Plugin on GitHub
learnMoreHref: https://github.com/Ekryd/sortpom
tags:
    - Source Code Policy
    - Formatting
dateAdded: 2026-05-29
---

SortPOM is a Maven plugin that sorts the elements of your `pom.xml` file into a canonical order. When multiple developers work on a project, `pom.xml` files frequently accumulate dependencies, plugins, and properties in inconsistent orders, leading to noisy diffs and harder-to-read files. SortPOM defines a deterministic canonical order for all POM elements, matching the official Maven POM schema. It can be run in fix mode to sort the file automatically, or in verify mode to fail the build if the POM is not already in the expected order. This is especially important in AI-assisted projects where AI agents may add dependencies in arbitrary positions.

## Code Example

```xml
<plugin>
    <groupId>com.github.ekryd.sortpom</groupId>
    <artifactId>sortpom-maven-plugin</artifactId>
    <version>4.0.0</version>
    <configuration>
        <predefinedSortOrder>custom_1</predefinedSortOrder>
        <lineSeparator>\n</lineSeparator>
        <encoding>${project.build.sourceEncoding}</encoding>
        <sortDependencies>scope,groupId,artifactId</sortDependencies>
        <sortPlugins>groupId,artifactId</sortPlugins>
        <sortProperties>true</sortProperties>
        <createBackupFile>false</createBackupFile>
        <expandEmptyNodes>false</expandEmptyNodes>
    </configuration>
    <executions>
        <execution>
            <goals>
                <!-- Use 'sort' goal to auto-fix, 'verify' goal for CI -->
                <goal>verify</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
