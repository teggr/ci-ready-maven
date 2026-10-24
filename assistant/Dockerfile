FROM eclipse-temurin:21-jdk-alpine@sha256:fd52084c35bbac78890d95d57707d31ffc9d7a3324199e89c06be2a9dc71bfa0 AS build
WORKDIR /workspace
COPY assistant/ assistant/
COPY _plugins/ _plugins/
COPY _guides/ _guides/
COPY site-config.xml site-config.xml
ARG SOURCE_COMMIT
RUN test -n "$SOURCE_COMMIT"
RUN cd assistant && ./mvnw -B -Dcatalogue.source-commit="$SOURCE_COMMIT" verify

FROM eclipse-temurin:21-jre-alpine@sha256:b48e02e0ea88d7bfb305335df94923bdef394905f4147dccd52e873cf17b1088
RUN addgroup -S assistant && adduser -S -G assistant assistant
WORKDIR /app
COPY --from=build --chown=assistant:assistant /workspace/assistant/target/ci-ready-maven-assistant-0.0.1-SNAPSHOT.jar app.jar
USER assistant
ENV SERVER_PORT=8080
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=65 -XX:+ExitOnOutOfMemoryError"
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget -q --spider "http://127.0.0.1:${SERVER_PORT}/actuator/health" || exit 1
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
