package io.github.teggr.assistant.catalogue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

class CatalogueIndexTest {
    private static final String COMMIT = "a".repeat(40);
    private Path root;

    @BeforeEach
    void fixture() throws Exception {
        root = Path.of("target", "catalogue-fixtures", UUID.randomUUID().toString());
        Files.createDirectories(root.resolve("_plugins"));
        Files.createDirectories(root.resolve("_guides"));
        Files.writeString(root.resolve("site-config.xml"),
                "<site><baseUrl>https://example.org/catalogue/</baseUrl></site>");
    }

    @AfterEach
    void cleanup() throws Exception {
        try (var paths = Files.walk(root)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) Files.delete(path);
        }
    }

    @Test
    void deterministicIndexRetainsCompleteMetadataBodyAndExamples() throws Exception {
        String source = """
                ---
                title: Example
                slug: different-slug
                description: Original description
                dateAdded: 2026-05-29
                lastRelease: "1.2"
                learnMoreHref: https://example.org/docs
                custom:
                  nested: [one, two]
                  nullable: null
                ---

                Original **body**.

                ```xml
                <plugin/>
                ```
                ~~~sh
                mvn verify
                ~~~
                """;
        Files.writeString(root.resolve("_guides/original-filename.md"), source);
        CatalogueIndex first = CatalogueIndexGenerator.generate(root, COMMIT);
        CatalogueIndex second = CatalogueIndexGenerator.generate(root, COMMIT);
        JsonMapper mapper = JsonMapper.builder().build();
        assertThat(mapper.writeValueAsBytes(first)).isEqualTo(mapper.writeValueAsBytes(second));
        assertThat(mapper.readValue(mapper.writeValueAsBytes(first), CatalogueIndex.class)).isEqualTo(first);
        CatalogueRecord record = first.records().getFirst();
        assertThat(record.id()).isEqualTo("guides/different-slug");
        assertThat(record.url()).isEqualTo("https://example.org/catalogue/guides/different-slug.html");
        assertThat(record.metadata()).containsEntry("dateAdded", "2026-05-29").containsEntry("lastRelease", "1.2");
        assertThat(record.metadata()).doesNotContainKeys("name", "category", "mavenCoordinates");
        assertThat((Map<?, ?>) record.metadata().get("custom")).hasSize(2);
        assertThat(((Map<?, ?>) record.metadata().get("custom")).get("nested")).isEqualTo(List.of("one", "two"));
        assertThat(((Map<?, ?>) record.metadata().get("custom")).containsKey("nullable")).isTrue();
        assertThat(((Map<?, ?>) record.metadata().get("custom")).get("nullable")).isNull();
        assertThat(record.body()).isEqualTo(source.substring(source.indexOf("---\n", 4) + 4));
        assertThat(record.examples()).containsExactly("<plugin/>\n", "mvn verify\n");
        assertThatThrownBy(() -> record.metadata().put("x", "y")).isInstanceOf(UnsupportedOperationException.class);
        Files.writeString(root.resolve("_guides/original-filename.md"), source + "More.");
        assertThat(CatalogueIndexGenerator.generate(root, COMMIT).contentSha256()).isNotEqualTo(first.contentSha256());
    }

    @Test
    void explicitCommitAllowsGenerationWithoutGitCheckout() throws Exception {
        write("one.md", "title: One\nslug: one");
        Path output = root.resolve("catalogue.json");
        CatalogueIndexGenerator.main(new String[] {root.toString(), output.toString(), COMMIT});
        CatalogueIndex index = JsonMapper.builder().build().readValue(Files.readAllBytes(output), CatalogueIndex.class);
        assertThat(index.sourceCommit()).isEqualTo(COMMIT);
        assertThatThrownBy(() -> CatalogueIndexGenerator.main(
                new String[] {root.toString(), output.toString(), "invalid"}))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validatesMissingTitlesUnsafeSlugsDuplicateIdsAndUnsafeLinks() throws Exception {
        write("one.md", "title: One\nslug: same");
        write("two.md", "title: Two\nslug: same");
        assertThatThrownBy(() -> CatalogueIndexGenerator.generate(root, COMMIT)).isInstanceOf(IllegalArgumentException.class);
        Files.delete(root.resolve("_plugins/two.md"));
        for (String metadata : List.of("slug: safe", "title: One\nslug: ../escape",
                "title: One\nslug: safe\nlearnMoreHref: javascript:alert(1)",
                "title: One\nslug: safe\nlearnMoreHref: http://example.org",
                "title: One\nslug: safe\nlearnMoreHref: ******example.org/",
                "title: One\ntitle: Two\nslug: safe",
                "title: !!java.lang.String bad\nslug: safe")) {
            write("one.md", metadata);
            assertThatThrownBy(() -> CatalogueIndexGenerator.generate(root, COMMIT)).isInstanceOf(RuntimeException.class);
        }
    }

    @Test
    void rejectsXmlExternalEntitiesAndInvalidRuntimeIndex() throws Exception {
        write("one.md", "title: One\nslug: one");
        Files.writeString(root.resolve("site-config.xml"),
                "<!DOCTYPE site [<!ENTITY x SYSTEM \"file:///etc/passwd\">]><site><baseUrl>&x;</baseUrl></site>");
        assertThatThrownBy(() -> CatalogueIndexGenerator.generate(root, COMMIT)).isInstanceOf(Exception.class);
        assertThatThrownBy(() -> new CatalogueIndex(2, COMMIT, "b".repeat(64), List.of()))
                .isInstanceOf(IllegalArgumentException.class);
        var bad = new CatalogueRecord("plugins/one", "One", "https://example.org/plugins/one.html",
                Map.of("title", "One", "slug", "one"), "", List.of("fabricated"));
        assertThatThrownBy(() -> new CatalogueIndex(1, COMMIT, "b".repeat(64), List.of(bad)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void packagesRealCatalogueAndLoadsItWithoutSpringOrModelCredentials() {
        CatalogueRepository repository = new CatalogueRepository();
        assertThat(repository.records()).hasSizeGreaterThanOrEqualTo(70);
        assertThat(repository.sourceCommit()).matches("[a-f0-9]{40,64}");
        assertThat(repository.contentSha256()).matches("[a-f0-9]{64}");
        assertThat(repository.records()).anySatisfy(record -> {
            assertThat(record.id()).isEqualTo("guides/master-your-maven-builds");
            assertThat(record.url()).endsWith("/guides/master-your-maven-builds.html");
        });
    }

    private void write(String filename, String metadata) throws Exception {
        Files.writeString(root.resolve("_plugins/" + filename), "---\n" + metadata + "\n---\nOriginal body.\n");
    }
}
