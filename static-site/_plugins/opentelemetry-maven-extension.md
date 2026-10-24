---
title: OpenTelemetry Maven Extension
description: Instruments Maven builds with distributed traces for CI observability.
order: 12
slug: opentelemetry-maven-extension
name: OpenTelemetry Maven Extension
category: CI Extensions / Observability
type: Maven Extension (OpenTelemetry / CNCF)
mavenCoordinates: io.opentelemetry.contrib:opentelemetry-maven-extension
lastRelease: "1.61.0-alpha"
learnMoreText: OpenTelemetry Maven Extension Documentation
learnMoreHref: https://github.com/open-telemetry/opentelemetry-java-contrib/tree/main/maven-extension
tags:
  - CI
  - Observability
  - Build Tracing
  - OpenTelemetry
  - Maven Extension
dateAdded: 2026-10-24
---

The OpenTelemetry Maven Extension instruments Maven builds and emits distributed traces that can be analyzed in observability platforms such as Jaeger, Zipkin, Grafana, or any OTLP-compatible backend. Maven phases, goals, and mojo executions are represented as spans, helping teams identify slow steps and track build performance over time in CI. Configure the exporter and endpoint with OpenTelemetry environment variables such as `OTEL_EXPORTER_OTLP_ENDPOINT`. Add the extension to `.mvn/extensions.xml` to enable tracing for Maven builds.

## Code Example

```xml
<!-- .mvn/extensions.xml -->
<extensions>
    <extension>
        <groupId>io.opentelemetry.contrib</groupId>
        <artifactId>opentelemetry-maven-extension</artifactId>
        <version>1.61.0-alpha</version>
    </extension>
</extensions>
```
