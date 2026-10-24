package io.github.teggr.assistant;

import io.github.teggr.assistant.catalogue.CatalogueRecord;
import io.github.teggr.assistant.catalogue.CatalogueSearchService;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AssistantServiceTests {
    private final CatalogueSearchService search = mock(CatalogueSearchService.class);
    private final ChatModel model = mock(ChatModel.class);
    @SuppressWarnings("unchecked")
    private final ObjectProvider<ChatModel> models = mock(ObjectProvider.class);
    private AssistantService service;
    private final CatalogueRecord jacoco = record("jacoco", "JaCoCo", "Measures test coverage. Enforces coverage thresholds.");
    private final CatalogueRecord checkstyle = record("checkstyle", "Checkstyle", "Checks source style. Fails on policy violations.");

    static AssistantProperties properties(Map<String, Object> overrides) {
        var values = new java.util.HashMap<String, Object>();
        values.put("ci-ready-maven.assistant.enabled", true);
        overrides.forEach((key, value) -> values.put("ci-ready-maven.assistant." + key, value));
        return new Binder(new MapConfigurationPropertySource(values))
                .bind("ci-ready-maven.assistant", Bindable.of(AssistantProperties.class)).get();
    }

    private static CatalogueRecord record(String id, String title, String body) {
        return new CatalogueRecord(id, title, "https://teggr.github.io/ci-ready-maven/" + id + ".html",
                Map.of(), body, List.of("<plugin><artifactId>" + id + "</artifactId></plugin>"));
    }

    @BeforeEach
    void setup() {
        service = new AssistantService(properties(Map.of()), search, models);
        when(models.getIfAvailable()).thenReturn(model);
        when(model.getOptions()).thenReturn(org.springframework.ai.chat.prompt.ChatOptions.builder().build());
        when(search.search(anyString(), anyInt())).thenReturn(List.of(new CatalogueSearchService.Match(jacoco, 10, true)));
        when(search.sufficient(anyString(), any())).thenReturn(true);
    }

    private void reply(String output) {
        when(model.call(any(Prompt.class))).thenReturn(new ChatResponse(List.of(new Generation(new AssistantMessage(output)))));
    }

    @Test
    void returnsOnlyVerbatimEvidenceAndExistingExamples() {
        reply("{\"selections\":[{\"sourceId\":\"jacoco\",\"quote\":\"Measures test coverage.\"}]}");
        var result = service.answer("Show JaCoCo configuration");
        assertThat(result.sources()).hasSize(1);
        assertThat(result.sources().getFirst().quotes()).containsExactly("Measures test coverage.");
        assertThat(result.sources().getFirst().examples()).containsExactly(jacoco.examples().getFirst());
        var captor = org.mockito.ArgumentCaptor.forClass(Prompt.class);
        verify(model).call(captor.capture());
        assertThat(captor.getValue().getContents()).contains("untrusted DATA", "Measures test coverage.");
    }

    @Test
    void rejectsInventedCodeSourcesPartialQuotesAndMalformedOutput() {
        for (String output : List.of(
                "{\"selections\":[{\"sourceId\":\"jacoco\",\"quote\":\"Invented answer.\"}]}",
                "{\"selections\":[{\"sourceId\":\"unknown\",\"quote\":\"Measures test coverage.\"}]}",
                "{\"selections\":[{\"sourceId\":\"jacoco\",\"quote\":\"test coverage.\"}]}",
                "{\"selections\":[{\"sourceId\":\"jacoco\",\"quote\":\"Measures test coverage.\"}],\"answer\":\"extra\"}",
                "{\"selections\":[{\"sourceId\":\"jacoco\",\"quote\":\"Measures test coverage.\"}]} {}",
                "{\"selections\":[]}", "not json", "{\"selections\":null}")) {
            reply(output);
            assertThat(service.answer("JaCoCo").message()).isEqualTo(AssistantService.ABSTENTION);
        }
    }

    @Test
    void insufficientEvidenceAndDisabledFeatureNeverCallModel() {
        when(search.sufficient(anyString(), any())).thenReturn(false);
        assertThat(service.answer("write arbitrary code").message()).isEqualTo(AssistantService.ABSTENTION);
        var disabled = new AssistantService(properties(Map.of("enabled", false)), search, models);
        assertThat(disabled.answer("JaCoCo").sources()).isEmpty();
        verifyNoInteractions(model);
    }

    @Test
    void absentProviderAndOutageAreSanitized() {
        when(models.getIfAvailable()).thenReturn(null);
        assertThat(service.answer("JaCoCo").message()).isEqualTo(AssistantService.UNAVAILABLE);
        when(models.getIfAvailable()).thenReturn(model);
        when(model.call(any(Prompt.class))).thenThrow(new IllegalStateException("secret-key and prompt"));
        assertThat(service.answer("JaCoCo").message()).isEqualTo(AssistantService.UNAVAILABLE);
    }

    @Test
    void comparisonMustCiteEveryNamedCandidate() {
        when(search.search(anyString(), anyInt())).thenReturn(List.of(
                new CatalogueSearchService.Match(jacoco, 10, true), new CatalogueSearchService.Match(checkstyle, 10, true)));
        reply("{\"selections\":[{\"sourceId\":\"jacoco\",\"quote\":\"Measures test coverage.\"}]}");
        assertThat(service.answer("Compare JaCoCo and Checkstyle").message()).isEqualTo(AssistantService.ABSTENTION);
        reply("{\"selections\":[{\"sourceId\":\"jacoco\",\"quote\":\"Measures test coverage.\"},"
                + "{\"sourceId\":\"checkstyle\",\"quote\":\"Checks source style.\"}]}");
        assertThat(service.answer("Compare JaCoCo and Checkstyle").sources()).hasSize(2);
    }

    @Test
    void sectionQualifiedIdsStillRequireEveryShortNamedComparisonCandidate() {
        var first = record("plugins/jacoco-maven-plugin", "JaCoCo Maven Plugin", "Measures test coverage.");
        var second = record("plugins/checkstyle-maven-plugin", "Apache Maven Checkstyle Plugin", "Checks source style.");
        when(search.search(anyString(), anyInt())).thenReturn(List.of(
                new CatalogueSearchService.Match(first, 10, true), new CatalogueSearchService.Match(second, 10, true)));
        reply("{\"selections\":[{\"sourceId\":\"plugins/jacoco-maven-plugin\",\"quote\":\"Measures test coverage.\"}]}");
        assertThat(service.answer("Compare JaCoCo and Checkstyle").message()).isEqualTo(AssistantService.ABSTENTION);
    }

    @Test
    void boundsSerializedContextAndRecordBodies() {
        service = new AssistantService(properties(Map.of("max-context-characters", 1000, "max-record-characters", 500)), search, models);
        var records = java.util.stream.IntStream.range(0, 10)
                .mapToObj(i -> record("tool" + i, "Tool " + i, "A complete sentence with escaped \"quotes\". ".repeat(100))).toList();
        var evidence = service.evidence(records);
        assertThat(evidence.values()).allMatch(e -> e.text().length() <= 500);
        String serialized = new tools.jackson.databind.json.JsonMapper().writeValueAsString(
                evidence.values().stream().map(e -> new AssistantService.ContextRecord(e.record().id(), e.record().title(), e.text())).toList());
        assertThat(serialized.length()).isLessThanOrEqualTo(1000);
        assertThat(evidence).isNotEmpty();
    }

    @Test
    void rejectsUntrustedCitationUrls() {
        for (String url : List.of("javascript:alert(1)", "https://evil.example/ci-ready-maven/tool",
                "https://teggr.github.io.evil.example/ci-ready-maven/", "https://teggr.github.io/other/",
                "https://user@teggr.github.io/ci-ready-maven/tool")) {
            assertThat(AssistantService.trustedUrl(url)).isFalse();
        }
    }

    @Test
    void holdsConcurrencyPermitUntilBlockingModelActuallyFinishes() throws Exception {
        service = new AssistantService(properties(Map.of("max-concurrent-requests", 1)), search, models);
        var entered = new CountDownLatch(1);
        var release = new CountDownLatch(1);
        when(model.call(any(Prompt.class))).thenAnswer(call -> {
            entered.countDown();
            if (!release.await(5, TimeUnit.SECONDS)) throw new IllegalStateException("test timeout");
            return new ChatResponse(List.of(new Generation(new AssistantMessage("{\"selections\":[]}"))));
        });
        try (var executor = Executors.newSingleThreadExecutor()) {
            var first = executor.submit(() -> service.answer("JaCoCo"));
            try {
                assertThat(entered.await(5, TimeUnit.SECONDS)).isTrue();
                assertThat(service.answer("JaCoCo").message()).contains("busy");
                verify(model, times(1)).call(any(Prompt.class));
            } finally {
                release.countDown();
            }
            first.get(5, TimeUnit.SECONDS);
        }
    }
}
