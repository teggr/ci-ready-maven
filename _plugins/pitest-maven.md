---
title: PIT Mutation Testing
description: Measure test effectiveness with mutation testing and enforce mutation coverage thresholds in CI.
order: 5
slug: pitest-maven
name: PIT Mutation Testing
category: Coverage
type: Maven Plugin
mavenCoordinates: org.pitest:pitest-maven
lastRelease: "1.30.1"
learnMoreText: PIT Maven Plugin Documentation
learnMoreHref: https://pitest.org/quickstart/maven/
tags:
  - Coverage
  - Mutation Testing
  - Quality Gate
dateAdded: 2026-10-24
---

PIT (Pitest) measures test effectiveness by introducing small defects into Java bytecode and rerunning the test suite. Mutations that survive indicate gaps in tests that line coverage alone may not reveal, making mutation score a stronger quality signal. The Maven plugin can enforce a minimum mutation threshold and fail CI when that threshold is not met. Mutation testing is significantly slower than JaCoCo, so it provides deeper coverage insight at a higher build cost.

## Code Example

```xml
<plugin>
    <groupId>org.pitest</groupId>
    <artifactId>pitest-maven</artifactId>
    <version>1.30.1</version>
    <configuration>
        <mutationThreshold>80</mutationThreshold>
    </configuration>
</plugin>
```

Run with `mvn test-compile org.pitest:pitest-maven:mutationCoverage`.
