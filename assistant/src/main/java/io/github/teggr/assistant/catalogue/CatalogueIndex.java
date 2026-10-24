package io.github.teggr.assistant.catalogue;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record CatalogueIndex(int schemaVersion, String sourceCommit, String contentSha256,
                             List<CatalogueRecord> records) {
    public CatalogueIndex {
        if (schemaVersion != 1 || sourceCommit == null || !sourceCommit.matches("[a-f0-9]{40,64}")
                || contentSha256 == null || !contentSha256.matches("[a-f0-9]{64}")
                || records == null || records.isEmpty()) {
            throw new IllegalArgumentException("Invalid catalogue index header");
        }
        records = List.copyOf(records);
        Set<String> ids = new HashSet<>();
        Set<String> urls = new HashSet<>();
        for (CatalogueRecord record : records) {
            if (record.id() == null || !record.id().matches("(plugins|guides)/[a-z0-9]+(?:-[a-z0-9]+)*")
                    || !ids.add(record.id()) || record.title() == null || record.title().isBlank()
                    || record.body() == null || !urls.add(record.url())) {
                throw new IllegalArgumentException("Invalid or duplicate catalogue record");
            }
            safeHttps(record.url());
            if (!URI.create(record.url()).getPath().endsWith("/" + record.id() + ".html")) {
                throw new IllegalArgumentException("Catalogue URL does not match its ID");
            }
            if (!record.title().equals(record.metadata().get("title"))
                    || !record.id().endsWith("/" + record.metadata().get("slug"))) {
                throw new IllegalArgumentException("Catalogue identity does not match metadata");
            }
            if (!record.examples().equals(CatalogueIndexGenerator.examples(record.body()))) {
                throw new IllegalArgumentException("Catalogue examples do not match body");
            }
            validateLinks(record.metadata());
        }
    }

    static void validateLinks(Map<String, Object> metadata) {
        metadata.forEach((key, value) -> {
            String lower = key.toLowerCase(java.util.Locale.ROOT);
            if (lower.endsWith("href") || lower.endsWith("url")) {
                if (!(value instanceof String link)) throw new IllegalArgumentException("Invalid metadata link");
                safeHttps(link);
            }
        });
    }

    static URI safeHttps(String value) {
        URI uri;
        try {
            uri = URI.create(value);
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("Invalid HTTPS URL", ex);
        }
        if (!"https".equals(uri.getScheme()) || uri.getHost() == null || uri.getUserInfo() != null
                || uri.getPort() != -1 && uri.getPort() != 443 || value.chars().anyMatch(Character::isWhitespace)) {
            throw new IllegalArgumentException("Expected an absolute, safe HTTPS URL");
        }
        return uri;
    }
}
