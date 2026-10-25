package io.github.teggr.qna.catalogue;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.teggr.qna.assistant.QaProperties;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatalogueSearchServiceTest {
    private CatalogueSearchService searchService;

    @BeforeEach
    void setUp() {
        QaProperties properties = new QaProperties();
        CatalogueIndex index = new CatalogueIndex(new ObjectMapper());
        searchService = new CatalogueSearchService(index, properties);
    }

    @Test
    void ranksExactNameAndCoordinatesHighly() {
        List<SearchResult> nameResults = searchService.search("google-java-format");
        List<SearchResult> coordinateResults = searchService.search("org.jacoco:jacoco-maven-plugin");

        assertTrue(nameResults.getFirst().record().id().equals("google-java-format"));
        assertTrue(nameResults.getFirst().score() >= 100);
        assertTrue(coordinateResults.getFirst().record().id().equals("jacoco-maven-plugin"));
        assertTrue(coordinateResults.getFirst().score() >= 100);
    }

    @Test
    void findsTagAndDescriptionMatchesAndAbstainsOnIrrelevantQuestions() {
        List<SearchResult> formatResults = searchService.search("formatting Java source code");
        List<SearchResult> irrelevant = searchService.search("where can I buy a bicycle");
        List<SearchResult> generic = searchService.search("Which Maven plugins?");

        assertTrue(formatResults.stream().anyMatch(result -> result.record().id().equals("google-java-format")));
        assertFalse(searchService.hasSufficientEvidence("where can I buy a bicycle", irrelevant));
        assertFalse(searchService.hasSufficientEvidence("Which Maven plugins?", generic));
    }
}
