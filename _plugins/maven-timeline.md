---
title: Maven Timeline
description: Generate a Gantt-style timeline of Maven phases and goals to spot CI build bottlenecks.
order: 12
slug: maven-timeline
name: Maven Timeline
category: CI Extensions / Maven Extensions for Build Time
type: Maven Extension (Takari)
mavenCoordinates: io.takari:maven-timeline
lastRelease: "2.0.3"
learnMoreText: Maven Timeline on GitHub
learnMoreHref: https://github.com/takari/maven-timeline
tags:
  - CI
  - Build Performance
  - Timeline
  - Visualisation
  - Maven Extension
dateAdded: 2026-07-18
---

Maven Timeline is a Takari Maven extension that produces a Gantt-chart-style view of your build execution. After a build completes, it writes `target/timeline.html` with phase and mojo start times and durations rendered as horizontal bars. This helps teams quickly identify bottlenecks and see where additional module parallelism may be possible. It pairs well with Takari Smart Builder and the Maven Build Cache Extension when optimizing CI throughput.

## Code Example

```xml
<!-- .mvn/extensions.xml -->
<extensions>
    <extension>
        <groupId>io.takari</groupId>
        <artifactId>maven-timeline</artifactId>
        <version>2.0.3</version>
    </extension>
</extensions>
```
