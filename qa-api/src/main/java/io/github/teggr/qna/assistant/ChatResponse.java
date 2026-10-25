package io.github.teggr.qna.assistant;

import java.util.List;

public record ChatResponse(AnswerStatus status, String answer, List<SourceDto> sources) {
}
