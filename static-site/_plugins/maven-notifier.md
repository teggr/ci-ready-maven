---
title: Maven Notifier
description: Sends end-of-build success/failure notifications so developers can step away from long Maven runs.
order: 12
slug: maven-notifier
name: Maven Notifier
category: CI Extensions / Developer Experience
type: Maven Extension (jcgay)
mavenCoordinates: fr.jcgay.maven:maven-notifier
lastRelease: "2.1.2"
learnMoreText: Maven Notifier on GitHub
learnMoreHref: https://github.com/jcgay/maven-notifier
tags:
  - CI
  - Developer Experience
  - Notifications
  - Maven Extension
dateAdded: 2026-09-27
---

Maven Notifier is an open-source Maven extension by Jean-Christophe Gay that sends a notification when a build finishes so developers do not have to keep watching the terminal. It reports success or failure for long-running local or CI-adjacent builds and supports multiple back ends, including macOS Notification Center, Growl, Linux `notify-send`, Windows Snarl, and Slack webhooks. This makes it easy to return to the build immediately when it completes instead of polling for status updates.

## Code Example

```xml
<!-- .mvn/extensions.xml -->
<extensions>
    <extension>
        <groupId>fr.jcgay.maven</groupId>
        <artifactId>maven-notifier</artifactId>
        <version>2.1.2</version>
    </extension>
</extensions>
```
