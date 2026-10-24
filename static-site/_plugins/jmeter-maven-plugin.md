---
title: JMeter Maven Plugin
description: Run Apache JMeter performance tests as part of Maven CI builds.
order: 12
slug: jmeter-maven-plugin
name: JMeter Maven Plugin
category: Performance Testing
type: Maven Plugin
mavenCoordinates: com.lazerycode.jmeter:jmeter-maven-plugin
lastRelease: "3.8.0"
learnMoreText: JMeter Maven Plugin Documentation
learnMoreHref: https://github.com/jmeter-maven-plugin/jmeter-maven-plugin
tags:
  - Test
  - Performance Testing
  - Load Testing
  - JMeter
dateAdded: 2026-10-24
---

The JMeter Maven Plugin integrates Apache JMeter performance and load tests into Maven builds. It downloads and configures JMeter automatically, then runs `.jmx` plans from `src/test/jmeter` during the `integration-test` phase. Result checks can fail the build when requests fail or the configured error-rate threshold is exceeded, while response-time limits can be asserted in the JMeter test plan. It can also generate HTML reports for reviewing test results in CI.

## Code Example

```xml
<plugin>
    <groupId>com.lazerycode.jmeter</groupId>
    <artifactId>jmeter-maven-plugin</artifactId>
    <version>3.8.0</version>
    <configuration>
        <generateReports>true</generateReports>
        <scanResultsForFailedRequests>true</scanResultsForFailedRequests>
        <errorRateThresholdInPercent>1</errorRateThresholdInPercent>
    </configuration>
    <executions>
        <execution>
            <id>configure-jmeter</id>
            <goals>
                <goal>configure</goal>
            </goals>
        </execution>
        <execution>
            <id>run-jmeter-tests</id>
            <goals>
                <goal>jmeter</goal>
            </goals>
        </execution>
        <execution>
            <id>check-jmeter-results</id>
            <goals>
                <goal>results</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
