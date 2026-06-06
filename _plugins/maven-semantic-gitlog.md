---
title: Maven Semantic Gitlog Plugin
description: Automate semantic versioning and changelog generation from Conventional Commits.
order: 12
slug: maven-semantic-gitlog
name: Maven Semantic Gitlog Plugin
category: Git / CI
type: Maven Plugin
mavenCoordinates: team.yi.maven.plugin:maven-semantic-gitlog
lastRelease: "0.22.27"
learnMoreText: maven-semantic-gitlog on GitHub
learnMoreHref: https://github.com/semantic-gitlog/maven-semantic-gitlog
tags:
  - Git
  - Changelog
  - Semantic Versioning
  - Conventional Commits
  - CI
dateAdded: 2026-06-06
---

maven-semantic-gitlog applies Semantic Versioning 2.0.0 by analysing Conventional Commit history and deriving the next version (major, minor, or patch). It can update the Maven project version and generate a formatted `CHANGELOG.md` from Mustache templates as part of your build lifecycle, making release automation and CI changelog workflows consistent across repositories.

## Code Example

```xml
<plugin>
    <groupId>team.yi.maven.plugin</groupId>
    <artifactId>maven-semantic-gitlog</artifactId>
    <version>0.22.27</version>
    <configuration>
        <updateProjectVersion>true</updateProjectVersion>
        <fileSets>
            <fileSet>
                <target>${project.basedir}/CHANGELOG.md</target>
                <template>${project.basedir}/config/gitlog/CHANGELOG.md.mustache</template>
            </fileSet>
        </fileSets>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>derive</goal>
                <goal>changelog</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
