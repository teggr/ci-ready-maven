---
title: TagList Maven Plugin
description: Generate TODO/FIXME technical debt reports during Maven site generation.
order: 12
slug: taglist-maven-plugin
name: TagList Maven Plugin
category: Quality / Reporting
type: Maven Plugin
mavenCoordinates: org.codehaus.mojo:taglist-maven-plugin
lastRelease: "3.2.2"
learnMoreText: TagList Maven Plugin Documentation
learnMoreHref: https://www.mojohaus.org/taglist-maven-plugin/
tags:
  - Quality
  - Technical Debt
  - Reporting
  - TODO
dateAdded: 2026-06-06
---

The TagList Maven Plugin generates a report of source code tags such as `TODO`, `FIXME`, and `@deprecated`. It is a reporting plugin that runs during Maven site generation, groups findings by tag type, and links entries back to source files. Teams can also define custom tag classes and matching strategies to track project-specific technical debt markers.

## Code Example

```xml
<reporting>
    <plugins>
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>taglist-maven-plugin</artifactId>
            <version>3.2.2</version>
            <configuration>
                <tagListOptions>
                    <tagClasses>
                        <tagClass>
                            <displayName>Technical Debt</displayName>
                            <tags>
                                <tag>
                                    <matchString>TODO</matchString>
                                    <matchType>exact</matchType>
                                </tag>
                                <tag>
                                    <matchString>FIXME</matchString>
                                    <matchType>exact</matchType>
                                </tag>
                                <tag>
                                    <matchString>@deprecated</matchString>
                                    <matchType>ignoreCase</matchType>
                                </tag>
                            </tags>
                        </tagClass>
                    </tagClasses>
                </tagListOptions>
            </configuration>
        </plugin>
    </plugins>
</reporting>
```
