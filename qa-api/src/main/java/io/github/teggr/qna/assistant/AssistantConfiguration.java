package io.github.teggr.qna.assistant;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AssistantConfiguration {
    @Bean
    public ObjectMapper catalogueObjectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }

    @Bean(destroyMethod = "shutdownNow")
    public ThreadPoolExecutor assistantExecutor(QaProperties properties) {
        return new ThreadPoolExecutor(
                properties.getMaxConcurrentModelCalls(),
                properties.getMaxConcurrentModelCalls(),
                0,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(properties.getModelQueueCapacity()),
                runnable -> {
                    Thread thread = new Thread(runnable, "qna-model-request");
                    thread.setDaemon(true);
                    return thread;
                },
                new ThreadPoolExecutor.AbortPolicy());
    }
}
