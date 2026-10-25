package io.github.teggr.qna.catalogue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CatalogueIndexerTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void generatesStableIndexAndRejectsMissingOrEmptySources() throws IOException {
        Path source = Files.createDirectory(temporaryDirectory.resolve("source"));
        Files.writeString(source.resolve("z.md"), markdown("z-tool", "Z tool"));
        Files.writeString(source.resolve("a.md"), markdown("a-tool", "A tool"));
        Path first = temporaryDirectory.resolve("first.json");
        Path second = temporaryDirectory.resolve("second.json");

        CatalogueIndexer.generate(source, first);
        CatalogueIndexer.generate(source, second);

        assertEquals(Files.readString(first), Files.readString(second));
        CatalogueIndexDocument document = new ObjectMapper().readValue(first.toFile(), CatalogueIndexDocument.class);
        assertEquals("a-tool", document.records().getFirst().id());
        assertEquals(2, document.records().size());

        Path empty = Files.createDirectory(temporaryDirectory.resolve("empty"));
        assertThrows(IOException.class, () -> CatalogueIndexer.read(empty));
        assertThrows(IOException.class, () -> CatalogueIndexer.read(temporaryDirectory.resolve("missing")));
    }

    @Test
    void rejectsDuplicateIdsAndRemovesStaleOutputBeforeFailing() throws IOException {
        Path source = Files.createDirectory(temporaryDirectory.resolve("duplicate-source"));
        Files.writeString(source.resolve("first.md"), markdown("same-tool", "First"));
        Files.writeString(source.resolve("second.md"), markdown("same-tool", "Second"));
        Path output = temporaryDirectory.resolve("index.json");
        Files.writeString(output, "stale index");

        assertThrows(IOException.class, () -> CatalogueIndexer.generate(source, output));
        assertTrue(Files.notExists(output));
    }

    private String markdown(String slug, String name) {
        return """
                ---
                name: %s
                slug: %s
                category: Static Analysis
                type: Maven Plugin
                ---
                description
                """.formatted(name, slug);
    }
}
