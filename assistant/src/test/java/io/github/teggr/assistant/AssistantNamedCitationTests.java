package io.github.teggr.assistant;

import io.github.teggr.assistant.catalogue.CatalogueRecord;
import io.github.teggr.assistant.catalogue.CatalogueRepository;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {
        "ci-ready-maven.assistant.enabled=true",
        "spring.ai.model.chat=none"
})
class AssistantNamedCitationTests {
    @Autowired AssistantService service;
    @Autowired CatalogueRepository repository;
    @MockitoBean ChatModel model;

    @BeforeEach
    void options() {
        when(model.getOptions()).thenReturn(ChatOptions.builder().build());
    }

    @Test
    void realJacocoCoberturaComparisonRejectsIncompleteEvidenceAndAcceptsBoth() {
        CatalogueRecord jacoco = record("plugins/jacoco-maven-plugin");
        CatalogueRecord cobertura = record("plugins/cobertura-maven-plugin");
        for (String question : List.of(
                "Compare JaCoCo and Cobertura",
                "Compare " + jacoco.title() + " and " + cobertura.title(),
                "Compare org.jacoco:jacoco-maven-plugin and org.codehaus.mojo:cobertura-maven-plugin")) {
            reply(List.of(selection(jacoco)));
            assertThat(service.answer(question).message()).as(question).isEqualTo(AssistantService.ABSTENTION);
            reply(List.of(selection(jacoco), selection(cobertura)));
            assertThat(service.answer(question).sources()).as(question)
                    .extracting(AssistantResponse.Source::id).containsExactlyInAnyOrder(jacoco.id(), cobertura.id());
        }
    }

    @Test
    void singleNamedConfigurationCannotUseAnotherRetrievedToolsQuoteOrExamples() {
        CatalogueRecord spotless = record("plugins/spotless-maven-plugin");
        CatalogueRecord pmd = record("plugins/maven-pmd-plugin");
        var evidence = service.evidence(List.of(spotless, pmd));
        var mapper = new tools.jackson.databind.json.JsonMapper();
        String unrelated = mapper.writeValueAsString(
                new AssistantService.SelectionEnvelope(List.of(selection(pmd))));
        assertThat(service.validate("Show Spotless configuration", unrelated, evidence).message())
                .isEqualTo(AssistantService.ABSTENTION);
        String requested = mapper.writeValueAsString(
                new AssistantService.SelectionEnvelope(List.of(selection(spotless))));
        var accepted = service.validate("Show Spotless configuration", requested, evidence);
        assertThat(accepted.sources()).extracting(AssistantResponse.Source::id).containsExactly(spotless.id());
        assertThat(accepted.sources().getFirst().examples()).isNotEmpty()
                .containsExactlyElementsOf(spotless.examples().stream().limit(2)
                        .filter(example -> example.length() <= 4000).toList());
    }

    private CatalogueRecord record(String id) {
        return repository.records().stream().filter(record -> record.id().equals(id)).findFirst().orElseThrow();
    }

    private AssistantService.Selection selection(CatalogueRecord record) {
        String text = service.evidence(List.of(record)).get(record.id()).text();
        var sentences = Pattern.compile("(?m)^[^#<`\\n][^\\n]*?[.!?](?=\\s|$)").matcher(text);
        while (sentences.find()) {
            String quote = sentences.group();
            if (quote.length() <= 1200 && AssistantService.completeExcerpt(text, quote))
                return new AssistantService.Selection(record.id(), quote);
        }
        throw new AssertionError("No complete bounded prose sentence in catalogue record " + record.id());
    }

    private void reply(List<AssistantService.Selection> selections) {
        String json = new tools.jackson.databind.json.JsonMapper()
                .writeValueAsString(new AssistantService.SelectionEnvelope(selections));
        when(model.call(any(Prompt.class))).thenReturn(new ChatResponse(List.of(new Generation(new AssistantMessage(json)))));
    }
}
