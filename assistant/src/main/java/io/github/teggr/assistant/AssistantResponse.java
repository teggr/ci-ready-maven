package io.github.teggr.assistant;

import java.util.List;
import java.util.Map;

public record AssistantResponse(String question, String message, List<Source> sources,
                                String sourceCommit, String contentSha256) {
    public AssistantResponse(String question, String message, List<Source> sources) {
        this(question, message, sources, "", "");
    }

    public record Source(String id, String title, String url, List<String> quotes, List<String> examples,
                         String learnMoreText, String learnMoreHref, Map<String, String> metadata) {
        public Source(String id, String title, String url, List<String> quotes, List<String> examples) {
            this(id, title, url, quotes, examples, "", "", Map.of());
        }
    }

    static AssistantResponse message(String question, String message) {
        return new AssistantResponse(question, message, List.of());
    }
}
