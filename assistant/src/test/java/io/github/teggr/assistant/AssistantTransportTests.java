package io.github.teggr.assistant;

import com.sun.net.httpserver.HttpServer;
import io.github.teggr.assistant.catalogue.CatalogueRecord;
import io.github.teggr.assistant.catalogue.CatalogueSearchService;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.ai.model.chat=openai",
        "spring.ai.openai.api-key=local-test-not-a-secret",
        "spring.ai.openai.chat.model=transport-test-model",
        "spring.ai.openai.chat.max-tokens=999999",
        "ci-ready-maven.assistant.enabled=true",
        "ci-ready-maven.assistant.max-answer-tokens=700",
        "ci-ready-maven.assistant.connect-timeout-millis=100",
        "ci-ready-maven.assistant.read-timeout-millis=1000"
})
@AutoConfigureMockMvc
class AssistantTransportTests {
    private static final AtomicInteger requests = new AtomicInteger();
    private static final AtomicReference<String> mode = new AtomicReference<>("success");
    private static final AtomicReference<String> requestBody = new AtomicReference<>("");
    private static final java.util.concurrent.ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private static final HttpServer server = startServer();
    @Autowired AssistantService service;
    @Autowired MockMvc mvc;
    @MockitoBean CatalogueSearchService search;

    private static HttpServer startServer() {
        try {
            var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            server.setExecutor(executor);
            server.createContext("/v1/chat/completions", exchange -> {
                requests.incrementAndGet();
                requestBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
                String body = """
                        {"id":"local","object":"chat.completion","created":1,"model":"test",
                         "choices":[{"index":0,"message":{"role":"assistant","content":"{\\"selections\\":[{\\"sourceId\\":\\"jacoco\\",\\"quote\\":\\"Measures test coverage.\\"}]}"},"finish_reason":"stop"}]}
                        """;
                int status = 200;
                if ("outage".equals(mode.get())) {
                    status = 503;
                    body = "{\"error\":{\"message\":\"private upstream diagnostic\",\"type\":\"server_error\"}}";
                } else if ("timeout".equals(mode.get())) {
                    try { Thread.sleep(2200); } catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
                }
                try {
                    byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(status, bytes.length);
                    exchange.getResponseBody().write(bytes);
                } catch (IOException ignored) {
                    // A timed-out client has already closed its connection.
                } finally {
                    exchange.close();
                }
            });
            server.start();
            return server;
        } catch (IOException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    @DynamicPropertySource
    static void localProvider(DynamicPropertyRegistry registry) {
        registry.add("spring.ai.openai.base-url", () -> "http://127.0.0.1:" + server.getAddress().getPort() + "/v1");
    }

    @BeforeEach
    void prepare() {
        requests.set(0);
        requestBody.set("");
        mode.set("success");
        var record = new CatalogueRecord("jacoco", "JaCoCo", "https://teggr.github.io/ci-ready-maven/jacoco.html",
                Map.of(), "Measures test coverage.", List.of());
        when(search.search(anyString(), anyInt())).thenReturn(List.of(new CatalogueSearchService.Match(record, 10, true)));
        when(search.sufficient(anyString(), anyList())).thenReturn(true);
    }

    @AfterAll
    static void stop() {
        server.stop(0);
        executor.shutdownNow();
    }

    @Test
    void openAiCompatibleSdkReturnsValidatedEvidence() {
        assertThat(service.answer("JaCoCo").sources()).hasSize(1);
        assertThat(requests.get()).isEqualTo(1);
        var body = new tools.jackson.databind.json.JsonMapper().readTree(requestBody.get());
        assertThat(body.path("model").asText()).isEqualTo("transport-test-model");
        assertThat(body.path("max_tokens").asInt()).isEqualTo(700);
        assertThat(body.path("temperature").asDouble()).isZero();
    }

    @Test
    void upstreamFailuresAreNotRetriedOrDisclosed() {
        mode.set("outage");
        assertThat(service.answer("JaCoCo").message()).isEqualTo(AssistantService.UNAVAILABLE);
        assertThat(requests.get()).isEqualTo(1);
    }

    @Test
    void actualHttpReadTimeoutBoundsBlockingCallWithoutRetries() {
        mode.set("timeout");
        long start = System.nanoTime();
        assertThat(service.answer("JaCoCo").message()).isEqualTo(AssistantService.UNAVAILABLE);
        assertThat(Duration.ofNanos(System.nanoTime() - start)).isLessThan(Duration.ofSeconds(3));
        assertThat(requests.get()).isEqualTo(1);
    }

    @Test
    void actuatorOnlyExposesSanitizedHealth() throws Exception {
        mvc.perform(get("/actuator/health")).andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"UP\"}"));
        mvc.perform(get("/actuator/env")).andExpect(status().isNotFound());
        mvc.perform(get("/actuator/configprops")).andExpect(status().isNotFound());
    }
}
