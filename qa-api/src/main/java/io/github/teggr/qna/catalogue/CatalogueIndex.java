package io.github.teggr.qna.catalogue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class CatalogueIndex {
    private final List<CatalogueRecord> records;
    private final Map<String, CatalogueRecord> recordsById;

    @Autowired
    public CatalogueIndex(ObjectMapper objectMapper) {
        this(objectMapper, new ClassPathResource("catalogue/catalogue-index.json"));
    }

    CatalogueIndex(ObjectMapper objectMapper, ClassPathResource resource) {
        try (InputStream input = resource.getInputStream()) {
            CatalogueIndexDocument document = objectMapper.readValue(input, CatalogueIndexDocument.class);
            validate(document);
            records = List.copyOf(document.records());
            recordsById = records.stream().collect(Collectors.toUnmodifiableMap(CatalogueRecord::id, Function.identity()));
        } catch (IOException | RuntimeException exception) {
            throw new IllegalStateException("Packaged catalogue index is missing or invalid", exception);
        }
    }

    public List<CatalogueRecord> records() {
        return records;
    }

    public CatalogueRecord findById(String id) {
        return recordsById.get(id);
    }

    private void validate(CatalogueIndexDocument document) {
        if (document == null || document.schemaVersion() != 1 || document.records() == null || document.records().isEmpty()) {
            throw new IllegalArgumentException("Unsupported or empty catalogue index");
        }
        Set<String> ids = new HashSet<>();
        for (CatalogueRecord record : document.records()) {
            if (record == null
                    || record.id() == null
                    || !record.id().matches("[a-z0-9]+(?:-[a-z0-9]+)*")
                    || record.title() == null
                    || record.title().isBlank()
                    || record.category() == null
                    || record.category().isBlank()
                    || record.type() == null
                    || record.type().isBlank()
                    || !ids.add(record.id())
                    || !("https://teggr.github.io/ci-ready-maven/plugins/" + record.id() + ".html")
                            .equals(record.catalogueUrl())) {
                throw new IllegalArgumentException("Invalid or duplicate catalogue record");
            }
        }
    }
}
