package io.github.teggr.qna.assistant;

import java.util.List;

public record ModelResponse(String status, String answer, List<String> sourceIds) {
}
