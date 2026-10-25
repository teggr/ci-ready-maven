package io.github.teggr.qna.assistant;

import jakarta.validation.constraints.NotBlank;

public record ChatRequest(@NotBlank(message = "The question must not be empty.") String question) {
}
