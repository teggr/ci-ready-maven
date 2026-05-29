---
name: Spotless Maven Plugin
category: Source Code Policy
type: Maven Plugin
mavenCoordinates: com.diffplug.spotless:spotless-maven-plugin
lastRelease: "2.44.5"
learnMoreText: Spotless Maven Plugin on GitHub
learnMoreHref: https://github.com/diffplug/spotless/tree/main/plugin-maven
tags:
    - Source Code Policy
    - Formatting
dateAdded: 2026-05-29
---

Spotless Maven Plugin is a source formatting and linting plugin that enforces deterministic code style rules during Maven builds. It supports multiple formatters and file types, including Java, Kotlin, Markdown, and more, making it useful for polyglot repositories. For Java projects, Spotless can integrate with google-java-format or palantir-java-format and fail CI when files are not correctly formatted. Teams can run `spotless:apply` locally to auto-fix formatting and rely on `spotless:check` in CI for enforcement.

## Code Example

```xml
<plugin>
    <groupId>com.diffplug.spotless</groupId>
    <artifactId>spotless-maven-plugin</artifactId>
    <version>2.44.5</version>
    <configuration>
        <java>
            <googleJavaFormat>
                <version>1.22.0</version>
            </googleJavaFormat>
        </java>
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
