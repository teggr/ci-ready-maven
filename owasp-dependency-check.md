---
name: OWASP Dependency-Check Maven Plugin
category: Security and Compliance
type: Maven Plugin
mavenCoordinates: org.owasp:dependency-check-maven
lastRelease: "12.1.1"
learnMoreText: OWASP Dependency-Check Documentation
learnMoreHref: https://jeremylong.github.io/DependencyCheck/dependency-check-maven/
tags:
    - Security and Compliance
    - Dependency Governance
dateAdded: 2026-05-29
---

OWASP Dependency-Check is a software composition analysis tool that identifies project dependencies with known publicly disclosed vulnerabilities by checking them against the National Vulnerability Database (NVD) and other sources. The Maven plugin integrates this check into the build lifecycle, generating vulnerability reports and optionally failing the build when vulnerabilities above a configurable CVSS score threshold are detected. It is an essential tool for supply chain security, helping teams identify vulnerable libraries before they reach production. The plugin supports Java, .NET, JavaScript, Ruby, and other ecosystems.

## Code Example

```xml
<plugin>
    <groupId>org.owasp</groupId>
    <artifactId>dependency-check-maven</artifactId>
    <version>12.1.1</version>
    <configuration>
        <failBuildOnCVSS>7</failBuildOnCVSS>
        <suppressionFile>owasp-suppressions.xml</suppressionFile>
        <nvdApiKey>${nvd.api.key}</nvdApiKey>
        <formats>
            <format>HTML</format>
            <format>JSON</format>
        </formats>
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
