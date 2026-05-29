---
name: License Maven Plugin
category: Security and Compliance
type: Maven Plugin
mavenCoordinates: com.mycila:license-maven-plugin
lastRelease: "4.6"
learnMoreText: License Maven Plugin on GitHub
learnMoreHref: https://github.com/mycila/license-maven-plugin
tags:
    - Security and Compliance
dateAdded: 2026-05-29
---

The Mycila License Maven Plugin checks and manages license headers in source files. In enterprise projects, legal compliance often requires that all source files contain the correct copyright header. This plugin can verify that every source file has the expected license header and fail the build if any file is missing or has an incorrect header. It can also automatically add or update license headers in fix mode. The plugin supports multiple license templates, customizable header formats for different file types, and can exclude generated sources. It is an essential part of open-source compliance and internal IP protection workflows.

## Code Example

```xml
<plugin>
    <groupId>com.mycila</groupId>
    <artifactId>license-maven-plugin</artifactId>
    <version>4.6</version>
    <configuration>
        <licenseSets>
            <licenseSet>
                <header>src/main/resources/license-header.txt</header>
                <excludes>
                    <exclude>**/README.md</exclude>
                    <exclude>src/test/resources/**</exclude>
                </excludes>
            </licenseSet>
        </licenseSets>
    </configuration>
    <executions>
        <execution>
            <goals>
                <!-- Use 'format' goal to auto-fix, 'check' goal for CI -->
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
