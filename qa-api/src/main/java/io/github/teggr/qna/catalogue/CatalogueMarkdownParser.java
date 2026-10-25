package io.github.teggr.qna.catalogue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Code;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.Node;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

public class CatalogueMarkdownParser {
    private static final Pattern CODE_FENCE = Pattern.compile("(?s)```[^\\r\\n]*\\R(.*?)```");
    private static final Pattern CODE_SECTION =
            Pattern.compile("(?is)##\\s+Code Example\\s*(.*?)(?=\\n##\\s+|\\z)");
    private static final String CATALOGUE_BASE_URL = "https://teggr.github.io/ci-ready-maven/plugins/";
    private final Parser markdownParser = Parser.builder().build();

    public CatalogueRecord parse(Path file) throws IOException {
        String source = Files.readString(file);
        if (!source.startsWith("---\n") && !source.startsWith("---\r\n")) {
            throw new IllegalArgumentException("Missing YAML front matter in " + file.getFileName());
        }
        int frontMatterStart = source.indexOf('\n') + 1;
        Matcher endMarker = Pattern.compile("(?m)^---\\s*$").matcher(source);
        if (!endMarker.find(frontMatterStart)) {
            throw new IllegalArgumentException("Unterminated YAML front matter in " + file.getFileName());
        }

        String frontMatter = source.substring(frontMatterStart, endMarker.start());
        String body = source.substring(endMarker.end()).strip();
        Map<String, Object> metadata = readMetadata(frontMatter, file);

        String name = optionalString(metadata, "name");
        if (name == null || name.isBlank()) {
            name = optionalString(metadata, "title");
        }
        String id = optionalString(metadata, "slug");
        if (id == null || id.isBlank()) {
            String filename = file.getFileName().toString();
            id = filename.substring(0, filename.lastIndexOf('.'));
        }
        validateId(id, file);

        String category = requiredString(metadata, "category", file);
        String type = requiredString(metadata, "type", file);
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Missing required field 'name' in " + file.getFileName());
        }
        String description = optionalString(metadata, "description");
        if (description == null || description.isBlank()) {
            String[] paragraphs = body.split("\\R\\s*\\R", 2);
            description = paragraphs.length == 0 ? "" : markdownText(paragraphs[0]);
        }

        Matcher codeSection = CODE_SECTION.matcher(body);
        String codeExample = "";
        if (codeSection.find()) {
            Matcher codeFence = CODE_FENCE.matcher(codeSection.group(1));
            if (codeFence.find()) {
                codeExample = codeFence.group(1).strip();
            }
        }

        return new CatalogueRecord(
                id,
                name,
                category,
                type,
                optionalString(metadata, "mavenCoordinates"),
                stringList(metadata.get("tags"), file),
                description,
                markdownText(body),
                codeExample,
                optionalString(metadata, "lastRelease"),
                CATALOGUE_BASE_URL + id + ".html",
                optionalString(metadata, "learnMoreHref"));
    }

    private String markdownText(String body) {
        Node document = markdownParser.parse(body);
        StringBuilder text = new StringBuilder();
        document.accept(new AbstractVisitor() {
            @Override
            public void visit(Text node) {
                text.append(node.getLiteral()).append(' ');
            }

            @Override
            public void visit(Code node) {
                text.append(node.getLiteral()).append(' ');
            }

            @Override
            public void visit(FencedCodeBlock node) {
                text.append(node.getLiteral()).append(' ');
            }

            @Override
            public void visit(SoftLineBreak node) {
                text.append(' ');
            }
        });
        return text.toString().strip();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> readMetadata(String text, Path file) {
        LoaderOptions options = new LoaderOptions();
        options.setAllowDuplicateKeys(false);
        Object loaded;
        try {
            loaded = new Yaml(new SafeConstructor(options)).load(text);
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException("Invalid YAML front matter in " + file.getFileName(), exception);
        }
        if (!(loaded instanceof Map<?, ?> map)) {
            throw new IllegalArgumentException("YAML front matter must be a mapping in " + file.getFileName());
        }
        return (Map<String, Object>) map;
    }

    private String requiredString(Map<String, Object> metadata, String key, Path file) {
        String value = optionalString(metadata, key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing required field '" + key + "' in " + file.getFileName());
        }
        return value;
    }

    private String optionalString(Map<String, Object> metadata, String key) {
        Object value = metadata.get(key);
        return value == null ? null : String.valueOf(value).strip();
    }

    private List<String> stringList(Object value, Path file) {
        if (value == null) {
            return List.of();
        }
        if (!(value instanceof List<?> list)) {
            throw new IllegalArgumentException("Field 'tags' must be a list in " + file.getFileName());
        }
        List<String> result = new ArrayList<>();
        for (Object item : list) {
            if (item == null || String.valueOf(item).isBlank()) {
                throw new IllegalArgumentException("Field 'tags' contains an empty value in " + file.getFileName());
            }
            result.add(String.valueOf(item).strip());
        }
        return List.copyOf(result);
    }

    private void validateId(String id, Path file) {
        if (!id.matches("[a-z0-9]+(?:-[a-z0-9]+)*")) {
            throw new IllegalArgumentException("Invalid slug '" + id + "' in " + file.getFileName());
        }
    }
}
