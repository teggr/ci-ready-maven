---
name: Palantir Java Format
category: Source Code Policy
type: Maven Plugin / CLI Tool
mavenCoordinates: com.palantir.javaformat:palantir-java-format
lastRelease: "2.50.0"
learnMoreText: palantir-java-format on GitHub
learnMoreHref: https://github.com/palantir/palantir-java-format
tags:
    - Source Code Policy
    - Formatting
dateAdded: 2026-05-29
---

Palantir Java Format is a fork and enhancement of google-java-format that produces consistently formatted Java code with some stylistic adjustments preferred by the Palantir engineering team, including different handling of method chaining and lambda formatting. Like google-java-format, it produces completely deterministic output, making formatting a non-issue in code review. It is also used by JHarmonizer as the formatting step after class member reordering, providing a complete source structure and formatting pipeline. The formatter can be integrated into Maven builds to enforce consistent formatting across all contributors and AI-generated code.

## Code Example

```xml
<!-- As a Maven plugin via the palantir-java-format-gradle-plugin equivalent,
     or via the spotless-maven-plugin integration -->
<plugin>
    <groupId>com.diffplug.spotless</groupId>
    <artifactId>spotless-maven-plugin</artifactId>
    <version>2.44.5</version>
    <configuration>
        <java>
            <palantirJavaFormat>
                <version>2.50.0</version>
            </palantirJavaFormat>
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
