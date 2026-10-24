//JAVA 25+
//DEPS org.apache.maven:maven-artifact:3.9.11

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

class AuditReleases {
  private static final Path ENTRIES = Path.of("static-site", "_plugins");
  private static final Pattern FRONT_MATTER =
      Pattern.compile("\\A---\\R(.*?)\\R---(?:\\R|$)", Pattern.DOTALL);
  private static final Pattern COORDINATES =
      Pattern.compile("(?m)^mavenCoordinates:[ \\t]*(?:\"([^\"]*)\"|'([^']*)'|([^\\s#]+))");
  private static final Pattern RELEASE_VALUE =
      Pattern.compile("(?m)^lastRelease:[ \\t]*(?:\"([^\"]*)\"|'([^']*)'|([^\\s#]+))");
  private static final Pattern RELEASE_LINE =
      Pattern.compile("(?m)^(lastRelease:[ \\t]*)(?:\"([^\"]*)\"|'([^']*)'|([^\\s#]+))([ \\t]*(?:#.*)?)$");
  private static final Pattern PRERELEASE =
      Pattern.compile(
          "(?i)(?:^|[._-])(alpha|beta|rc|cr|snapshot|preview|milestone|m|ea|dev|nightly)(?:[._-]?\\d+)?(?:$|[._-])");
  private static final Pattern COORDINATE =
      Pattern.compile("[A-Za-z0-9_-]+(?:\\.[A-Za-z0-9_-]+)*:[A-Za-z0-9_.-]+");
  private static final HttpClient HTTP =
      HttpClient.newBuilder()
          .connectTimeout(Duration.ofSeconds(20))
          .followRedirects(HttpClient.Redirect.NORMAL)
          .build();

  public static void main(String... args) throws Exception {
    boolean dryRun = args.length == 1 && args[0].equals("--dry-run");
    if (args.length > 0 && !dryRun) {
      throw new IllegalArgumentException("Usage: jbang .github/scripts/audit-releases.java [--dry-run]");
    }

    List<Path> entries;
    try (var files = Files.list(ENTRIES)) {
      entries =
          files
              .filter(path -> path.getFileName().toString().endsWith(".md"))
              .sorted(Comparator.comparing(path -> path.getFileName().toString()))
              .toList();
    }

    List<Update> updates = new ArrayList<>();
    List<Review> reviews = new ArrayList<>();
    for (Path entry : entries) {
      String markdown = Files.readString(entry, StandardCharsets.UTF_8);
      Matcher frontMatterMatcher = FRONT_MATTER.matcher(markdown);
      if (!frontMatterMatcher.find()) {
        reviews.add(new Review(entry, null, null, "missing YAML front matter"));
        continue;
      }

      String frontMatter = frontMatterMatcher.group(1);
      String coordinates = firstValue(COORDINATES, frontMatter);
      String currentRelease = firstValue(RELEASE_VALUE, frontMatter);
      if (coordinates == null || !COORDINATE.matcher(coordinates).matches()) {
        reviews.add(
            new Review(entry, coordinates, null, "missing or malformed mavenCoordinates"));
        continue;
      }
      if (currentRelease == null || currentRelease.isBlank()) {
        reviews.add(
            new Review(entry, coordinates, metadataUrl(coordinates), "missing lastRelease"));
        continue;
      }

      String metadataUrl = metadataUrl(coordinates);
      List<String> publishedVersions;
      try {
        publishedVersions = getPublishedVersions(metadataUrl);
      } catch (NotFoundException e) {
        reviews.add(
            new Review(entry, coordinates, metadataUrl, "Maven Central has no artifact metadata"));
        continue;
      }

      String latestStable =
          publishedVersions.stream()
              .filter(version -> !PRERELEASE.matcher(version).find())
              .max(
                  (left, right) ->
                      new ComparableVersion(left).compareTo(new ComparableVersion(right)))
              .orElse(null);
      if (latestStable == null) {
        reviews.add(
            new Review(entry, coordinates, metadataUrl, "no stable release could be identified"));
        continue;
      }

      if (new ComparableVersion(latestStable).compareTo(new ComparableVersion(currentRelease)) > 0) {
        updates.add(new Update(entry, markdown, latestStable));
        System.out.printf(
            "UPDATE %s: %s -> %s (%s)%n",
            entry, currentRelease, latestStable, metadataUrl);
      } else if (PRERELEASE.matcher(currentRelease).find()) {
        reviews.add(
            new Review(
                entry,
                coordinates,
                metadataUrl,
                "catalogue version "
                    + currentRelease
                    + " is a prerelease; latest stable is "
                    + latestStable));
      } else {
        System.out.printf("OK %s: %s (latest stable: %s)%n", entry, currentRelease, latestStable);
      }
    }

    for (Update update : updates) {
      String updatedMarkdown = replaceRelease(update.markdown(), update.latest());
      if (!dryRun) {
        Files.writeString(update.path(), updatedMarkdown, StandardCharsets.UTF_8);
      }
    }
    for (Review review : reviews) {
      System.out.printf(
          "REVIEW %s: %s%s%s%n",
          review.path(),
          review.reason(),
          review.coordinates() == null ? "" : " (" + review.coordinates() + ")",
          review.metadataUrl() == null ? "" : " [" + review.metadataUrl() + "]");
    }
    System.out.printf(
        "Checked %d catalogue entries; %d updates%s; %d entries need review.%n",
        entries.size(), updates.size(), dryRun ? " previewed" : "", reviews.size());
  }

  private static String firstValue(Pattern pattern, String text) {
    Matcher matcher = pattern.matcher(text);
    if (!matcher.find()) {
      return null;
    }
    for (int group = 1; group <= matcher.groupCount(); group++) {
      if (matcher.group(group) != null) {
        return matcher.group(group).trim();
      }
    }
    return null;
  }

  private static String metadataUrl(String coordinates) {
    String[] parts = coordinates.split(":", 2);
    return "https://repo.maven.apache.org/maven2/"
        + parts[0].replace('.', '/')
        + "/"
        + parts[1]
        + "/maven-metadata.xml";
  }

  private static List<String> getPublishedVersions(String url)
      throws IOException, InterruptedException, ParserConfigurationException, SAXException {
    HttpResponse<String> response =
        HTTP.send(
            HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/xml")
                .header("User-Agent", "ci-ready-maven-release-audit")
                .GET()
                .build(),
            HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    if (response.statusCode() == 404) {
      throw new NotFoundException(url);
    }
    if (response.statusCode() != 200) {
      throw new IOException(
          "Maven Central returned HTTP " + response.statusCode() + " for " + url);
    }

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
    factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
    factory.setXIncludeAware(false);
    factory.setExpandEntityReferences(false);
    Document document =
        factory.newDocumentBuilder().parse(new InputSource(new StringReader(response.body())));

    List<String> versions = new ArrayList<>();
    NodeList versionNodes = document.getElementsByTagNameNS("*", "version");
    if (versionNodes.getLength() == 0) {
      versionNodes = document.getElementsByTagName("version");
    }
    for (int i = 0; i < versionNodes.getLength(); i++) {
      String version = versionNodes.item(i).getTextContent().trim();
      if (!version.isEmpty()) {
        versions.add(version);
      }
    }
    if (versions.isEmpty()) {
      throw new IOException("Maven metadata contained no versions for " + url);
    }
    return versions;
  }

  private static String replaceRelease(String markdown, String latest) throws IOException {
    Matcher frontMatterMatcher = FRONT_MATTER.matcher(markdown);
    if (!frontMatterMatcher.find()) {
      throw new IOException("Missing YAML front matter while updating catalogue entry");
    }
    String frontMatter = frontMatterMatcher.group(1);
    Matcher releaseMatcher = RELEASE_LINE.matcher(frontMatter);
    if (!releaseMatcher.find()) {
      throw new IOException("Could not locate lastRelease while updating catalogue entry");
    }
    String quote = releaseMatcher.group(2) != null ? "\"" : releaseMatcher.group(3) != null ? "'" : "";
    String replacement =
        releaseMatcher.group(1) + quote + latest + quote + releaseMatcher.group(5);
    String updatedFrontMatter =
        releaseMatcher.replaceFirst(Matcher.quoteReplacement(replacement));
    return markdown.substring(0, frontMatterMatcher.start(1))
        + updatedFrontMatter
        + markdown.substring(frontMatterMatcher.end(1));
  }

  private record Update(Path path, String markdown, String latest) {}

  private record Review(Path path, String coordinates, String metadataUrl, String reason) {}

  private static final class NotFoundException extends IOException {
    private NotFoundException(String url) {
      super("Maven Central metadata not found: " + url);
    }
  }
}
