package io.github.teggr.qna.catalogue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public final class CatalogueIndexer {
    private CatalogueIndexer() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException("Usage: CatalogueIndexer <markdown-directory> <output-file>");
        }
        generate(Path.of(args[0]), Path.of(args[1]));
    }

    public static CatalogueIndexDocument read(Path sourceDirectory) throws IOException {
        if (!Files.isDirectory(sourceDirectory) || !Files.isReadable(sourceDirectory)) {
            throw new IOException("Catalogue source directory is missing or unreadable: " + sourceDirectory);
        }

        CatalogueMarkdownParser parser = new CatalogueMarkdownParser();
        List<CatalogueRecord> records;
        try (Stream<Path> files = Files.list(sourceDirectory)) {
            List<Path> markdownFiles =
                    files.filter(Files::isRegularFile)
                            .filter(file -> file.getFileName().toString().endsWith(".md"))
                            .sorted(Comparator.comparing(file -> file.getFileName().toString()))
                            .toList();
            if (markdownFiles.isEmpty()) {
                throw new IOException("No Markdown catalogue records found in " + sourceDirectory);
            }
            records = markdownFiles.stream()
                    .map(file -> {
                        try {
                            return parser.parse(file);
                        } catch (IOException | IllegalArgumentException exception) {
                            throw new CatalogueIndexException(exception);
                        }
                    })
                    .sorted(Comparator.comparing(CatalogueRecord::id))
                    .toList();
        } catch (CatalogueIndexException exception) {
            throw new IOException("Could not parse catalogue record", exception.getCause());
        }

        long uniqueIds = records.stream().map(CatalogueRecord::id).distinct().count();
        if (uniqueIds != records.size()) {
            throw new IOException("Duplicate catalogue record slug detected in " + sourceDirectory);
        }
        return new CatalogueIndexDocument(1, records);
    }

    public static void generate(Path sourceDirectory, Path outputFile) throws IOException {
        Files.deleteIfExists(outputFile);
        CatalogueIndexDocument index = read(sourceDirectory);
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        Path parent = outputFile.toAbsolutePath().getParent();
        Files.createDirectories(parent);
        Path temporaryFile = Files.createTempFile(parent, "catalogue-index-", ".json");
        try {
            mapper.writeValue(temporaryFile.toFile(), index);
            Files.move(temporaryFile, outputFile, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    private static final class CatalogueIndexException extends RuntimeException {
        private CatalogueIndexException(Exception cause) {
            super(cause);
        }
    }
}
