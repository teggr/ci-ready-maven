---
title: Tidy Maven Plugin
description: Enforce canonical pom.xml element ordering to keep CI diffs predictable.
order: 12
slug: tidy-maven-plugin
name: Tidy Maven Plugin
category: Source Code Policy
type: Maven Plugin
mavenCoordinates: org.codehaus.mojo:tidy-maven-plugin
lastRelease: "1.4.0"
learnMoreText: Tidy Maven Plugin Documentation
learnMoreHref: https://www.mojohaus.org/tidy-maven-plugin/
tags:
  - Source Code Policy
  - Formatting
dateAdded: 2026-06-06
---

The Tidy Maven Plugin rewrites `pom.xml` files to the canonical element order recommended by the Maven team, which reduces noisy pull request diffs and keeps project metadata predictable across contributors. Teams can run `mvn tidy:pom` locally to auto-fix ordering, then enforce the same policy in CI with `tidy:check`. This makes it a practical companion to SortPOM when you want Maven-convention ordering specifically from MojoHaus tooling. In CI pipelines, binding `check` to the lifecycle ensures non-canonical POM ordering fails fast before release artifacts are produced.

## Code Example

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>tidy-maven-plugin</artifactId>
    <version>1.4.0</version>
    <executions>
        <execution>
            <id>tidy-check</id>
            <phase>verify</phase>
            <goals>
                <!-- Use 'pom' goal to auto-fix, 'check' goal for CI -->
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
