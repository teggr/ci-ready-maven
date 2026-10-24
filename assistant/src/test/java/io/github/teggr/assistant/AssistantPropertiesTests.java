package io.github.teggr.assistant;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import static org.assertj.core.api.Assertions.assertThat;

class AssistantPropertiesTests {
    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(AssistantConfiguration.class);

    @Test
    void disabledByDefaultAndAllDefaultsAreBounded() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context.getBean(AssistantProperties.class).enabled()).isFalse();
        });
    }

    @Test
    void rejectsUnsafeLimits() {
        for (String value : new String[]{"max-concurrent-requests=0", "max-question-length=999999",
                "max-request-bytes=0", "max-results=99", "max-context-characters=999999",
                "max-record-characters=0", "max-selections=100", "max-excerpt-characters=0",
                "max-output-characters=999999", "requests-per-window=0", "rate-window-seconds=0",
                "max-rate-limit-clients=999999", "connect-timeout-millis=0", "read-timeout-millis=999999"}) {
            runner.withPropertyValues("ci-ready-maven.assistant." + value)
                    .run(context -> assertThat(context).hasFailed());
        }
    }
}
