package io.github.teggr.assistant.catalogue;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.json.JsonMapper;

@Repository
public final class CatalogueRepository {
    private final CatalogueIndex index;

    @Autowired
    public CatalogueRepository() {
        this(load());
    }

    public CatalogueRepository(CatalogueIndex index) {
        this.index = java.util.Objects.requireNonNull(index);
    }

    public List<CatalogueRecord> records() { return index.records(); }
    public String sourceCommit() { return index.sourceCommit(); }
    public String contentSha256() { return index.contentSha256(); }

    private static CatalogueIndex load() {
        try (InputStream stream = CatalogueRepository.class.getResourceAsStream("/catalogue.json")) {
            if (stream == null) throw new IllegalStateException("Missing packaged catalogue.json");
            return JsonMapper.builder().build().readValue(stream, CatalogueIndex.class);
        } catch (IOException | RuntimeException ex) {
            throw new IllegalStateException("Cannot load validated catalogue index", ex);
        }
    }
}
