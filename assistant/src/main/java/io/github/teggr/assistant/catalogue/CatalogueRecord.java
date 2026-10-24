package io.github.teggr.assistant.catalogue;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public record CatalogueRecord(String id, String title, String url,
                              Map<String, Object> metadata, String body, List<String> examples) {
    public CatalogueRecord {
        if (metadata == null || examples == null) {
            throw new IllegalArgumentException("Missing catalogue metadata or examples");
        }
        metadata = freeze(metadata);
        examples = List.copyOf(examples);
    }

    static Map<String, Object> freeze(Map<?, ?> source) {
        Map<String, Object> copy = new TreeMap<>();
        source.forEach((key, value) -> {
            if (!(key instanceof String text)) {
                throw new IllegalArgumentException("Metadata keys must be strings");
            }
            copy.put(text, freezeValue(value));
        });
        return Collections.unmodifiableMap(copy);
    }

    private static Object freezeValue(Object value) {
        if (value instanceof Map<?, ?> map) return freeze(map);
        if (value instanceof List<?> list) return list.stream().map(CatalogueRecord::freezeValue).toList();
        if (value == null || value instanceof String || value instanceof Number || value instanceof Boolean) return value;
        throw new IllegalArgumentException("Unsupported YAML metadata type");
    }
}
