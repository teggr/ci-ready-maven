package io.github.teggr.assistant.catalogue;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CatalogueSearchServiceTest {
    private final CatalogueSearchService search = new CatalogueSearchService(new CatalogueRepository());

    @Test
    void exactAliasesRankBeforeIncidentalBodyMentions() {
        for (String alias : List.of("JaCoCo", "jacoco-maven-plugin", "org.jacoco:jacoco-maven-plugin")) {
            var results = search.search("Tell me about " + alias, 4);
            assertThat(results.getFirst().record().id()).isEqualTo("plugins/jacoco-maven-plugin");
            assertThat(results.getFirst().exact()).isTrue();
            assertThat(search.sufficient("Tell me about " + alias, results)).isTrue();
        }
        assertThat(search.search("test coverage thresholds", 4).getFirst().record().id())
                .isEqualTo("plugins/jacoco-maven-plugin");
        assertThat(search.sufficient("test coverage thresholds", search.search("test coverage thresholds", 4))).isTrue();
    }

    @Test
    void sharedNamedAliasesReturnFullRepositoryIds() {
        assertThat(search.namedRecordIds("Compare JaCoCo and Cobertura"))
                .containsExactlyInAnyOrder("plugins/jacoco-maven-plugin", "plugins/cobertura-maven-plugin");
        assertThat(search.namedRecordIds("Compare Spotless and PMD"))
                .containsExactlyInAnyOrder("plugins/spotless-maven-plugin", "plugins/maven-pmd-plugin");
        assertThat(search.namedRecordIds("org.jacoco:jacoco-maven-plugin"))
                .containsExactly("plugins/jacoco-maven-plugin");
        assertThat(search.namedRecordIds(null)).isEmpty();
        assertThat(search.namedRecordIds(" ")).isEmpty();
        assertThat(search.namedRecordIds("Compare static-analysis plugins")).isEmpty();
    }

    @Test
    void comparisonsRequireAllNamedRecordsAndSupportedQuestion() {
        String question = "Compare JaCoCo and Cobertura";
        var matches = search.search(question, 4);
        assertThat(matches.stream().filter(CatalogueSearchService.Match::exact).map(match -> match.record().id()))
                .containsExactlyInAnyOrder("plugins/jacoco-maven-plugin", "plugins/cobertura-maven-plugin");
        assertThat(search.sufficient(question, matches)).isTrue();
        assertThat(search.sufficient(question, search.search(question, 1))).isFalse();
        assertThat(search.sufficient("Compare JaCoCo and unknownbanana", search.search("Compare JaCoCo and unknownbanana", 4))).isFalse();
        assertThat(search.sufficient("Compare JaCoCo and Cobertura license", search.search(question, 4))).isFalse();
        assertThat(search.sufficient("Compare static-analysis plugins", search.search("Compare static-analysis plugins", 1))).isFalse();
        assertThat(search.sufficient("Compare static-analysis plugins for COBOL",
                search.search("Compare static-analysis plugins for COBOL", 4))).isFalse();
    }

    @Test
    void genericOffTopicAndAbsentCapabilitiesAbstain() {
        for (String question : List.of("Maven plugins for CI builds", "Write a banana cake recipe",
                "Does JaCoCo support quantumbanana?", "What is JaCoCo's license?",
                "Does JaCoCo support Java 99?", "What is the latest JaCoCo version?",
                "Does Spotless support COBOL?", "Which plugins enforce quantum formatting?",
                "What is the version of Master Your Maven Builds?")) {
            assertThat(search.sufficient(question, search.search(question, 5))).as(question).isFalse();
        }
        assertThat(search.sufficient("What is the recorded JaCoCo version?",
                search.search("What is the recorded JaCoCo version?", 4))).isTrue();
        assertThat(search.search("Maven plugins for CI builds", 5)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {
                "Which plugins can enforce Java code formatting?",
                "How can I measure test coverage in Maven?",
                "Compare the static-analysis plugins in this catalogue.",
                "Show me the documented Maven configuration for Spotless.",
                "Which plugins are relevant to dependency governance?",
                "What is Spotless?"})
    void everyUiStarterHasSufficientRealCatalogueEvidence(String question) {
        var matches = search.search(question, 4);
        assertThat(search.sufficient(question, matches)).as(question + " -> " + matches.stream()
                .map(match -> match.record().id()).toList()).isTrue();
    }

    @Test
    void deterministicBoundedSearchHandlesEmptyInput() {
        assertThat(search.search(null, 4)).isEmpty();
        assertThat(search.search("", 4)).isEmpty();
        assertThat(search.search("coverage", 0)).isEmpty();
        assertThat(search.search("coverage", 2)).hasSize(2).isEqualTo(search.search("coverage", 2));
        assertThat(search.search("coverage", 100)).hasSizeLessThanOrEqualTo(20);
        assertThat(search.sufficient("coverage", List.of())).isFalse();
    }
}
