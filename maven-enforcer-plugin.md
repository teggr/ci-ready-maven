---
name: Maven Enforcer Plugin
category: Dependency Governance
type: Maven Plugin
mavenCoordinates: org.apache.maven.plugins:maven-enforcer-plugin
lastRelease: "3.5.0"
learnMoreText: Maven Enforcer Plugin Documentation
learnMoreHref: https://maven.apache.org/enforcer/maven-enforcer-plugin/
tags:
    - Dependency Governance
    - Build Reproducibility
dateAdded: 2026-05-29
---

The Maven Enforcer Plugin provides goals to control environmental constraints such as Maven version, JDK version, and OS. It also allows you to enforce project dependency rules including banning specific dependencies, requiring dependency convergence, preventing SNAPSHOT dependencies in releases, and checking for duplicate dependencies. It is one of the foundational tools for enforcing build reproducibility and dependency governance in enterprise Maven projects.

## Code Example

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-enforcer-plugin</artifactId>
    <version>3.5.0</version>
    <executions>
        <execution>
            <id>enforce</id>
            <goals>
                <goal>enforce</goal>
            </goals>
            <configuration>
                <rules>
                    <requireMavenVersion>
                        <version>[3.9,)</version>
                    </requireMavenVersion>
                    <requireJavaVersion>
                        <version>[21,)</version>
                    </requireJavaVersion>
                    <dependencyConvergence/>
                    <banDuplicatePomDependencyVersions/>
                    <requireReleaseDeps>
                        <onlyWhenRelease>true</onlyWhenRelease>
                    </requireReleaseDeps>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```
