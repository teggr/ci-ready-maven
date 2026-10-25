package io.github.teggr.qna.assistant;

import io.github.teggr.qna.catalogue.CatalogueIndex;
import io.github.teggr.qna.catalogue.CatalogueRecord;
import io.github.teggr.qna.catalogue.SearchResult;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class AnswerValidator {
    private final CatalogueIndex index;
    private final QaProperties properties;

    public AnswerValidator(CatalogueIndex index, QaProperties properties) {
        this.index = index;
        this.properties = properties;
    }

    public ChatResponse validate(ModelResponse response, List<SearchResult> retrieved) {
        if (response == null || response.status() == null || response.answer() == null) {
            throw invalidResponse();
        }

        AnswerStatus status;
        try {
            status = AnswerStatus.valueOf(response.status().strip());
        } catch (IllegalArgumentException exception) {
            throw invalidResponse();
        }

        String answer = response.answer().strip();
        if (answer.isEmpty() || answer.length() > properties.getMaxAnswerCharacters()) {
            throw invalidResponse();
        }

        if (status == AnswerStatus.INSUFFICIENT_EVIDENCE) {
            return new ChatResponse(status, answer, List.of());
        }

        if (response.sourceIds() == null || response.sourceIds().isEmpty()) {
            return insufficient("The catalogue evidence does not support a reliable answer.");
        }

        Set<String> retrievedIds = new HashSet<>();
        retrieved.forEach(result -> retrievedIds.add(result.record().id()));
        Set<String> seen = new HashSet<>();
        List<SourceDto> sources = new ArrayList<>();
        for (String sourceId : response.sourceIds()) {
            if (sourceId == null || !retrievedIds.contains(sourceId)) {
                return insufficient("The catalogue evidence does not support a reliable answer.");
            }
            if (!seen.add(sourceId)) {
                continue;
            }
            CatalogueRecord record = index.findById(sourceId);
            if (record == null) {
                return insufficient("The catalogue evidence does not support a reliable answer.");
            }
            sources.add(new SourceDto(record.id(), record.title(), record.catalogueUrl()));
        }
        if (sources.isEmpty()) {
            return insufficient("The catalogue evidence does not support a reliable answer.");
        }
        return new ChatResponse(status, answer, List.copyOf(sources));
    }

    private ChatResponse insufficient(String answer) {
        return new ChatResponse(AnswerStatus.INSUFFICIENT_EVIDENCE, answer, List.of());
    }

    private AssistantException invalidResponse() {
        return new AssistantException(AssistantException.Reason.INVALID_MODEL_RESPONSE);
    }
}
