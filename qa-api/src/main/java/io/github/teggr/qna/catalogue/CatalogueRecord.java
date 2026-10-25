package io.github.teggr.qna.catalogue;

import java.util.List;

public record CatalogueRecord(
        String id,
        String title,
        String category,
        String type,
        String mavenCoordinates,
        List<String> tags,
        String description,
        String content,
        String codeExample,
        String lastRelease,
        String catalogueUrl,
        String documentationUrl) {
}
