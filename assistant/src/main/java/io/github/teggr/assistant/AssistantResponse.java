package io.github.teggr.assistant;

import java.util.List;

public record AssistantResponse(String question, String message, List<Source> sources) {
    public record Source(String id, String title, String url, List<String> quotes, List<String> examples) {}

    static AssistantResponse message(String question, String message) {
        return new AssistantResponse(question, message, List.of());
    }
}
