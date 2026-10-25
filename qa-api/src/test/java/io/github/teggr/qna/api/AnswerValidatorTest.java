package io.github.teggr.qna.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.teggr.qna.assistant.AnswerStatus;
import io.github.teggr.qna.assistant.AnswerValidator;
import io.github.teggr.qna.assistant.AssistantException;
import io.github.teggr.qna.assistant.ChatResponse;
import io.github.teggr.qna.assistant.ModelResponse;
import io.github.teggr.qna.catalogue.CatalogueIndex;
import io.github.teggr.qna.catalogue.CatalogueRecord;
import io.github.teggr.qna.catalogue.SearchResult;
import io.github.teggr.qna.assistant.QaProperties;
import java.util.List;
import org.junit.jupiter.api.Test;

class AnswerValidatorTest {
    private final CatalogueIndex index = new CatalogueIndex(new ObjectMapper());
    private final AnswerValidator validator = new AnswerValidator(index, new QaProperties());

    @Test
    void resolvesCanonicalSourcesAndDeduplicatesThem() {
        CatalogueRecord record = index.findById("google-java-format");
        ChatResponse response = validator.validate(
                new ModelResponse("ANSWERED", "Use google-java-format.", List.of(record.id(), record.id())),
                List.of(new SearchResult(record, 100)));

        assertEquals(1, response.sources().size());
        assertEquals(record.title(), response.sources().getFirst().title());
        assertEquals(record.catalogueUrl(), response.sources().getFirst().catalogueUrl());
    }

    @Test
    void rejectsSourcesThatWereNotRetrievedAndAbstainsWithoutSources() {
        CatalogueRecord retrieved = index.findById("google-java-format");
        assertEquals(
                AnswerStatus.INSUFFICIENT_EVIDENCE,
                validator.validate(
                                new ModelResponse("ANSWERED", "Unsupported.", List.of("invented-source")),
                                List.of(new SearchResult(retrieved, 100)))
                        .status());
        assertEquals(
                List.of(),
                validator.validate(
                                new ModelResponse("INSUFFICIENT_EVIDENCE", "Not in catalogue.", List.of()),
                                List.of(new SearchResult(retrieved, 100)))
                        .sources());
        assertThrows(
                AssistantException.class,
                () -> validator.validate(new ModelResponse("UNKNOWN", "Bad", List.of()), List.of()));
    }
}
