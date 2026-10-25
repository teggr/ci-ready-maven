package io.github.teggr.qna.catalogue;

import java.util.List;

public record CatalogueIndexDocument(int schemaVersion, List<CatalogueRecord> records) {
}
