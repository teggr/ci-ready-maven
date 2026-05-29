---
name: google-java-format
category: Source Code Policy
type: Maven Plugin / CLI Tool
mavenCoordinates: com.spotify.fmt:fmt-maven-plugin
lastRelease: "2.27"
learnMoreText: google-java-format on GitHub
learnMoreHref: https://github.com/google/google-java-format
tags:
    - Source Code Policy
    - Formatting
dateAdded: 2026-05-29
---

google-java-format is an opinionated code formatter for Java developed by Google. It reformats Java source code to comply with the Google Java Style Guide, eliminating all formatting debates by making the output completely deterministic. When integrated via the `fmt-maven-plugin`, it can either reformat code automatically in fix mode or verify that code is already correctly formatted in check mode (suitable for CI). The formatter handles imports, blank lines, indentation, line wrapping, and overall layout. Because the format is completely reproducible, pull request diffs contain only behavior changes, not formatting noise.

## Code Example

```xml
<plugin>
    <groupId>com.spotify.fmt</groupId>
    <artifactId>fmt-maven-plugin</artifactId>
    <version>2.27</version>
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
