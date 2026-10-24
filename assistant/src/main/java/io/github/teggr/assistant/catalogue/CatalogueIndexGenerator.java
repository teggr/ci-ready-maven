package io.github.teggr.assistant.catalogue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.Tag;
import tools.jackson.databind.json.JsonMapper;

public final class CatalogueIndexGenerator {
    private static final Pattern FRONT = Pattern.compile("\\A---\\R(.*?)\\R---(?:\\R|\\z)", Pattern.DOTALL);
    private static final Pattern FENCE = Pattern.compile("(?m)^ {0,3}(`{3,}|~{3,})([^\\r\\n]*)\\R");

    private CatalogueIndexGenerator() {}

    public static void main(String[] args) throws Exception {
        if (args.length < 2 || args.length > 3) {
            throw new IllegalArgumentException("Expected repository root, output file and optional source commit");
        }
        Path root = Path.of(args[0]).toAbsolutePath().normalize();
        Path output = Path.of(args[1]);
        String commit = args.length == 3 && !args[2].isBlank() && !"auto".equals(args[2])
                ? args[2] : sourceCommit(root);
        CatalogueIndex index = generate(root, commit);
        Files.createDirectories(output.toAbsolutePath().getParent());
        Files.write(output, JsonMapper.builder().build().writeValueAsBytes(index));
    }

    public static CatalogueIndex generate(Path root, String commit) throws Exception {
        byte[] config = Files.readAllBytes(root.resolve("site-config.xml"));
        var factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        var document = factory.newDocumentBuilder().parse(new java.io.ByteArrayInputStream(config));
        var bases = document.getElementsByTagName("baseUrl");
        if (bases.getLength() != 1) throw new IllegalArgumentException("Expected one site baseUrl");
        String base = bases.item(0).getTextContent().strip();
        var uri = CatalogueIndex.safeHttps(base);
        if (uri.getQuery() != null || uri.getFragment() != null) throw new IllegalArgumentException("Invalid site baseUrl");
        if (!base.endsWith("/")) base += "/";
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        hash(digest, "site-config.xml", config);
        List<CatalogueRecord> records = new ArrayList<>();
        for (String section : List.of("guides", "plugins")) {
            try (var paths = Files.list(root.resolve("_" + section))) {
                for (Path path : paths.filter(p -> p.getFileName().toString().endsWith(".md")).sorted().toList()) {
                    byte[] bytes = Files.readAllBytes(path);
                    hash(digest, "_" + section + "/" + path.getFileName(), bytes);
                    records.add(parse(section, base, new String(bytes, StandardCharsets.UTF_8)));
                }
            }
        }
        records.sort(java.util.Comparator.comparing(CatalogueRecord::id));
        return new CatalogueIndex(1, commit, HexFormat.of().formatHex(digest.digest()), records);
    }

    static CatalogueRecord parse(String section, String base, String markdown) {
        var front = FRONT.matcher(markdown);
        if (!front.find()) throw new IllegalArgumentException("Missing YAML front matter");
        LoaderOptions options = new LoaderOptions();
        options.setAllowDuplicateKeys(false);
        options.setMaxAliasesForCollections(20);
        SafeConstructor constructor = new SafeConstructor(options) {
            { yamlConstructors.put(Tag.TIMESTAMP, new ConstructYamlStr()); }
        };
        Object loaded = new Yaml(constructor).load(front.group(1));
        if (!(loaded instanceof Map<?, ?> map)) throw new IllegalArgumentException("Invalid front matter");
        Map<String, Object> metadata = CatalogueRecord.freeze(map);
        if (!(metadata.get("slug") instanceof String slug) || !slug.matches("[a-z0-9]+(?:-[a-z0-9]+)*")
                || !(metadata.get("title") instanceof String title) || title.isBlank()) {
            throw new IllegalArgumentException("Missing title or unsafe slug");
        }
        String body = markdown.substring(front.end());
        return new CatalogueRecord(section + "/" + slug, title, base + section + "/" + slug + ".html",
                metadata, body, examples(body));
    }

    static List<String> examples(String body) {
        List<String> examples = new ArrayList<>();
        var opening = FENCE.matcher(body);
        int cursor = 0;
        while (opening.find(cursor)) {
            String fence = opening.group(1);
            var closing = Pattern.compile("(?m)^ {0,3}" + Pattern.quote(fence.substring(0, 1))
                    + "{" + fence.length() + ",}[ \\t]*(?:\\R|\\z)").matcher(body);
            if (!closing.find(opening.end())) throw new IllegalArgumentException("Unclosed code fence");
            examples.add(body.substring(opening.end(), closing.start()));
            cursor = closing.end();
        }
        return List.copyOf(examples);
    }

    private static void hash(MessageDigest digest, String name, byte[] bytes) {
        digest.update(name.getBytes(StandardCharsets.UTF_8));
        digest.update((byte) 0);
        digest.update(Integer.toString(bytes.length).getBytes(StandardCharsets.US_ASCII));
        digest.update((byte) 0);
        digest.update(bytes);
    }

    private static String sourceCommit(Path root) throws IOException, InterruptedException {
        Process process = new ProcessBuilder("git", "-C", root.toString(), "rev-parse", "HEAD")
                .redirectError(ProcessBuilder.Redirect.DISCARD).start();
        if (!process.waitFor(5, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            throw new IOException("Timed out resolving source commit");
        }
        String commit = new String(process.getInputStream().readNBytes(256), StandardCharsets.US_ASCII).strip();
        if (process.exitValue() != 0 || !commit.matches("[a-f0-9]{40,64}")) throw new IOException("Cannot resolve source commit");
        return commit;
    }
}
