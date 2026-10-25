package io.github.teggr.qna.catalogue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CatalogueMarkdownParserTest {
    @TempDir
    Path temporaryDirectory;

    private final CatalogueMarkdownParser parser = new CatalogueMarkdownParser();

    @Test
    void extractsMetadataDescriptionContentAndCodeExample() throws IOException {
        Path source = temporaryDirectory.resolve("example-plugin.md");
        Files.writeString(source, """
                ---
                title: Example plugin
                name: Example plugin
                slug: example-plugin
                category: Static Analysis
                type: Maven Plugin
                mavenCoordinates: com.example:example-plugin
                lastRelease: "1.2.3"
                learnMoreHref: https://example.com/docs
                tags:
                  - Static Analysis
                  - CI
                ---
                Example description.

                ## Code Example

                ```xml
                <plugin><artifactId>example-plugin</artifactId></plugin>
                ```
                """);

        CatalogueRecord record = parser.parse(source);

        assertEquals("example-plugin", record.id());
        assertEquals("Example plugin", record.title());
        assertEquals("com.example:example-plugin", record.mavenCoordinates());
        assertEquals(2, record.tags().size());
        assertTrue(record.content().contains("Example description."));
        assertTrue(record.codeExample().contains("<artifactId>example-plugin</artifactId>"));
        assertEquals(
                "https://teggr.github.io/ci-ready-maven/plugins/example-plugin.html",
                record.catalogueUrl());
        assertEquals("https://example.com/docs", record.documentationUrl());
    }

    @Test
    void supportsOptionalFieldsAndFilenameBasedIds() throws IOException {
        Path source = temporaryDirectory.resolve("fallback-id.md");
        Files.writeString(source, """
                ---
                name: Tool without optional metadata
                category: Coverage
                type: CLI Tool
                ---
                A description.
                """);

        CatalogueRecord record = parser.parse(source);

        assertEquals("fallback-id", record.id());
        assertEquals(null, record.mavenCoordinates());
        assertTrue(record.tags().isEmpty());
        assertEquals("A description.", record.description());
        assertEquals("", record.codeExample());
        assertEquals(null, record.documentationUrl());
    }

    @Test
    void rejectsMalformedYamlAndInvalidSlugs() throws IOException {
        Path malformed = temporaryDirectory.resolve("malformed.md");
        Files.writeString(malformed, "---\nname: [invalid\n---\n");
        assertThrows(IllegalArgumentException.class, () -> parser.parse(malformed));

        Path invalidSlug = temporaryDirectory.resolve("invalid.md");
        Files.writeString(invalidSlug, """
                ---
                name: Invalid slug
                slug: ../invalid
                category: Coverage
                type: CLI Tool
                ---
                content
                """);
        assertThrows(IllegalArgumentException.class, () -> parser.parse(invalidSlug));
    }
}
