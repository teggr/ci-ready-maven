/// usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 21+
//DEPS com.j2html:j2html:1.6.0
//DEPS org.commonmark:commonmark:0.21.0
//DEPS org.commonmark:commonmark-ext-yaml-front-matter:0.21.0
//DEPS org.commonmark:commonmark-ext-heading-anchor:0.21.0

import j2html.rendering.IndentedHtml;
import j2html.tags.DomContent;
import j2html.tags.specialized.HtmlTag;
import org.commonmark.ext.front.matter.YamlFrontMatterExtension;
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import static j2html.TagCreator.*;

import java.io.*;
import java.nio.file.*;
import java.nio.file.StandardCopyOption;
import java.util.*;

void main(String... args) throws IOException {

  System.out.println("Building CI Ready Maven static site...");

  // Create output directory if it doesn't exist
  Path outputDirectory = Paths.get("_site");
  if (!Files.exists(outputDirectory)) {
    Files.createDirectory(outputDirectory);
    System.out.println("Created output directory");
  }

  // collect all markdown data in a map, key is html file name, value is the data from the markdown file
  Map<String, Map<String, List<String>>> markdownData = new TreeMap<>();

  // start iterating over the markdown files in the root folder, excluding README.md
  Files.list(Paths.get("."))
    .filter(path -> path.toString().endsWith(".md") && !path.getFileName().toString().equals("README.md"))
    .forEach(path -> {
      try {
        System.out.println("Processing " + path);
        String markdown = Files.readString(path);

        // Parse markdown with CommonMark
        Parser parser = Parser.builder()
          .extensions(List.of(YamlFrontMatterExtension.create(), HeadingAnchorExtension.create()))
          .build();
        HtmlRenderer renderer = HtmlRenderer.builder()
          .extensions(List.of(YamlFrontMatterExtension.create(), HeadingAnchorExtension.create()))
          .build();
        org.commonmark.node.Node document = parser.parse(markdown);
        // Extract YAML front matter
        YamlFrontMatterVisitor frontMatterVisitor = new YamlFrontMatterVisitor();
        document.accept(frontMatterVisitor);

        String html = renderer.render(document);
        // Fix formatting: add newline after opening code tag for proper indentation
        html = html.replaceAll("(<code[^>]*>)([^ \\n])", "$1\n$2");

        // Write HTML to output directory
        HtmlTag htmlTag = output(toolPage(frontMatterVisitor.getData(), rawHtml(html)),
          SeoMetadata.forTool(frontMatterVisitor.getData(), path.getFileName().toString().replace(".md", ".html")));

        StringWriter writer = new StringWriter();
        writer.append(htmlTag.render(IndentedHtml.inMemory()));

        String finalHtml = writer.toString();

        String htmlFileName = path.getFileName().toString().replace(".md", ".html");

        markdownData.put(htmlFileName, frontMatterVisitor.getData());

        Path htmlPath = outputDirectory.resolve(htmlFileName);
        Files.writeString(htmlPath, finalHtml);
        System.out.println("Generated " + htmlFileName);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

  // Generate index.html
  StringWriter writer = new StringWriter();
  writer.append(output(indexPage(markdownData), SeoMetadata.index()).render(IndentedHtml.inMemory()));
  String html = writer.toString();
  Path indexPath = outputDirectory.resolve("index.html");
  Files.writeString(indexPath, html);
  System.out.println("Generated index.html");

  // Generate grid partials for htmx sorting
  StringWriter gridAlphaWriter = new StringWriter();
  gridAlphaWriter.append(gridAlphabetical(markdownData).render(IndentedHtml.inMemory()));
  Files.writeString(outputDirectory.resolve("grid-alphabetical.html"), gridAlphaWriter.toString());
  System.out.println("Generated grid-alphabetical.html");

  StringWriter gridTagWriter = new StringWriter();
  gridTagWriter.append(gridByTag(markdownData).render(IndentedHtml.inMemory()));
  Files.writeString(outputDirectory.resolve("grid-by-tag.html"), gridTagWriter.toString());
  System.out.println("Generated grid-by-tag.html");

  StringWriter gridDateWriter = new StringWriter();
  gridDateWriter.append(gridByDate(markdownData).render(IndentedHtml.inMemory()));
  Files.writeString(outputDirectory.resolve("grid-by-date.html"), gridDateWriter.toString());
  System.out.println("Generated grid-by-date.html");

  // Collect unique tags from all markdown data
  Set<String> uniqueTags = collectUniqueTags(markdownData);

  // Generate tag pages
  for (String tag : uniqueTags) {
    String tagSlug = tagToSlug(tag);
    String tagFileName = "tag-" + tagSlug + ".html";
    StringWriter tagWriter = new StringWriter();
    tagWriter.append(output(tagPage(tag, markdownData), SeoMetadata.forTag(tag)).render(IndentedHtml.inMemory()));
    String tagHtml = tagWriter.toString();
    Path tagPath = outputDirectory.resolve(tagFileName);
    Files.writeString(tagPath, tagHtml);
    System.out.println("Generated " + tagFileName + " for tag: " + tag);
  }

  // copy the css directory and all the files inside to the output directory
  Path cssSource = Paths.get("css");
  Path cssTarget = outputDirectory.resolve("css");
  if (Files.exists(cssSource) && Files.isDirectory(cssSource)) {
    Files.walk(cssSource).forEach(source -> {
      try {
        Path target = cssTarget.resolve(cssSource.relativize(source));
        if (Files.isDirectory(source)) {
          if (!Files.exists(target)) {
            Files.createDirectory(target);
          }
        } else {
          Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    System.out.println("Copied css directory");
  } else {
    System.out.println("No css directory found, skipping copy");
  }

  System.out.println("Site build completed successfully!");

}

private DomContent toolPage(Map<String, List<String>> data, DomContent content) {

  String name = data.getOrDefault("name", List.of("Unknown Tool")).get(0);
  String category = data.getOrDefault("category", List.of("")).get(0);
  String type = data.getOrDefault("type", List.of("")).get(0);
  String mavenCoordinates = data.getOrDefault("mavenCoordinates", List.of("")).get(0);
  String lastRelease = data.getOrDefault("lastRelease", List.of("")).get(0);
  String learnMoreText = data.getOrDefault("learnMoreText", List.of("Learn More")).get(0);
  String learnMoreHref = data.getOrDefault("learnMoreHref", List.of("#")).get(0);
  List<String> tags = data.getOrDefault("tags", List.of());

  return div(
    div(
      a("← Back")
        .withHref("index.html")
        .attr("onclick", "history.back(); return false;")
        .withClass("back-link")
    ),
    each(
      h1(name),
      div(
        each(
          !category.isEmpty() ? p(strong("Category"), text(category)) : text(""),
          !type.isEmpty() ? p(strong("Type"), text(type)) : text(""),
          !mavenCoordinates.isEmpty() ? p(strong("Maven Coordinates"), code(mavenCoordinates)) : text(""),
          !lastRelease.isEmpty() ? p(strong("Latest Release"), text(lastRelease)) : text("")
        )
      ).withClass("project-meta"),
      a(
        text(learnMoreText),
        i().withClass("bi bi-box-arrow-up-right")
      ).withHref(learnMoreHref).withTarget("_blank"),
      div(
        each(tags, tag -> {
          String tagSlug = tagToSlug(tag);
          String tagFileName = "tag-" + tagSlug + ".html";
          return a(tag)
            .withHref(tagFileName)
            .attr("hx-get", tagFileName)
            .attr("hx-target", "body")
            .attr("hx-swap", "innerHTML transition:true show:window:top")
            .attr("hx-push-url", "true")
            .withClass("tag");
        })
      ).withClass("tags"),
      content
    )
  ).withId("main-content");

}

private static DomContent projectCard(String htmlFileName, Map<String, List<String>> data) {
  String toolName = data.getOrDefault("name", List.of("Tool")).get(0);
  String toolCategory = data.getOrDefault("category", List.of("")).get(0);
  String toolType = data.getOrDefault("type", List.of("")).get(0);
  List<String> tags = data.getOrDefault("tags", List.of());

  return a(
    div(
      div(toolCategory).withClass("project-category"),
      div(toolName).withClass("project-name"),
      div(toolType).withClass("project-type"),
      tags.isEmpty() ? text("") : div(
        each(tags, tag -> span(tag).withClass("card-tag"))
      ).withClass("card-tags")
    ).withClass("project-card-content")
  )
    .withHref(htmlFileName)
    .attr("hx-get", htmlFileName)
    .attr("hx-target", "body")
    .attr("hx-swap", "innerHTML transition:true show:window:top")
    .attr("hx-push-url", "true")
    .withClass("project-card");
}

private static Set<String> collectUniqueTags(Map<String, Map<String, List<String>>> markdownData) {
  Set<String> uniqueTags = new HashSet<>();
  for (Map<String, List<String>> data : markdownData.values()) {
    List<String> tags = data.getOrDefault("tags", List.of());
    uniqueTags.addAll(tags);
  }
  return uniqueTags;
}

private static String tagToSlug(String tag) {
  return tag.toLowerCase()
    .replaceAll("\\s+", "-")
    .replaceAll("[^a-z0-9-]", "");
}

static DomContent tagPage(String tag, Map<String, Map<String, List<String>>> markdownData) {
  // Filter tools by tag
  Map<String, Map<String, List<String>>> filteredTools = new TreeMap<>();
  for (Map.Entry<String, Map<String, List<String>>> entry : markdownData.entrySet()) {
    List<String> projectTags = entry.getValue().getOrDefault("tags", List.of());
    if (projectTags.contains(tag)) {
      filteredTools.put(entry.getKey(), entry.getValue());
    }
  }

  return div(
    div(
      a("← Back")
        .withHref("index.html")
        .attr("onclick", "history.back(); return false;")
        .withClass("back-link")
    ),
    h1("Tools: " + tag),
    div(
      each(filteredTools.entrySet(), entry -> projectCard(entry.getKey(), entry.getValue()))
    ).withClass("project-list")
  ).withId("main-content");
}

// Grid content for initial page render (alphabetical)
static DomContent gridAlphabeticalContent(Map<String, Map<String, List<String>>> markdownData) {
  return div(
    each(markdownData.entrySet(), entry -> projectCard(entry.getKey(), entry.getValue()))
  ).withClass("project-list").withId("browse-section");
}

// Partial response for htmx: alphabetical grid + OOB button swap
static DomContent gridAlphabetical(Map<String, Map<String, List<String>>> markdownData) {
  return each(
    gridAlphabeticalContent(markdownData),
    sortButtons("alphabetical")
  );
}

// Grid content for tag-grouped view
static DomContent gridByTagContent(Map<String, Map<String, List<String>>> markdownData) {
  Set<String> uniqueTags = new TreeSet<>(collectUniqueTags(markdownData));

  return div(
    each(uniqueTags, tag -> {
      // Filter tools by tag
      Map<String, Map<String, List<String>>> filteredTools = new TreeMap<>();
      for (Map.Entry<String, Map<String, List<String>>> entry : markdownData.entrySet()) {
        List<String> projectTags = entry.getValue().getOrDefault("tags", List.of());
        if (projectTags.contains(tag)) {
          filteredTools.put(entry.getKey(), entry.getValue());
        }
      }
      return div(
        h2(tag).withClass("tag-group-title"),
        div(
          each(filteredTools.entrySet(), entry -> projectCard(entry.getKey(), entry.getValue()))
        ).withClass("project-list")
      ).withClass("tag-group");
    })
  ).withId("browse-section");
}

// Partial response for htmx: tag-grouped grid + OOB button swap
static DomContent gridByTag(Map<String, Map<String, List<String>>> markdownData) {
  return each(
    gridByTagContent(markdownData),
    sortButtons("category")
  );
}

// Grid content for date-sorted view
static DomContent gridByDateContent(Map<String, Map<String, List<String>>> markdownData) {
  Map<String, Map<String, List<String>>> sortedData = new LinkedHashMap<>();
  markdownData.entrySet().stream()
    .sorted((e1, e2) -> {
      String date1 = e1.getValue().getOrDefault("dateAdded", List.of("1900-01-01")).get(0);
      String date2 = e2.getValue().getOrDefault("dateAdded", List.of("1900-01-01")).get(0);
      return date2.compareTo(date1);
    })
    .forEach(entry -> sortedData.put(entry.getKey(), entry.getValue()));

  return div(
    each(sortedData.entrySet(), entry -> projectCard(entry.getKey(), entry.getValue()))
  ).withClass("project-list").withId("browse-section");
}

// Partial response for htmx: date-sorted grid + OOB button swap
static DomContent gridByDate(Map<String, Map<String, List<String>>> markdownData) {
  return each(
    gridByDateContent(markdownData),
    sortButtons("date")
  );
}

// Generate the segmented control buttons for sorting with proper active state
static DomContent sortButtons(String activeSort) {
  return div(
    button(
      i().withClass("bi bi-sort-alpha-down"),
      text(" Alphabetical")
    )
      .withClass("sort-btn" + ("alphabetical".equals(activeSort) ? " active" : ""))
      .attr("hx-get", "grid-alphabetical.html")
      .attr("hx-target", "#browse-section")
      .attr("hx-swap", "outerHTML transition:true"),
    button(
      i().withClass("bi bi-tags"),
      text(" Category")
    )
      .withClass("sort-btn" + ("category".equals(activeSort) ? " active" : ""))
      .attr("hx-get", "grid-by-tag.html")
      .attr("hx-target", "#browse-section")
      .attr("hx-swap", "outerHTML transition:true"),
    button(
      i().withClass("bi bi-clock-history"),
      text(" Recently Added")
    )
      .withClass("sort-btn" + ("date".equals(activeSort) ? " active" : ""))
      .attr("hx-get", "grid-by-date.html")
      .attr("hx-target", "#browse-section")
      .attr("hx-swap", "outerHTML transition:true")
  )
    .withClass("sort-button-group")
    .withId("sort-buttons")
    .attr("hx-swap-oob", "true");
}

static DomContent indexPage(Map<String, Map<String, List<String>>> markdownData) {
  Set<String> uniqueTags = collectUniqueTags(markdownData);

  return div(
    // Hero Section with split layout
    div(
      // Left column - Title and CTA
      div(
        h1("CI Ready Maven").withClass("hero-title"),
        a(
          i().withClass("bi bi-github"),
          text(" Contribute on GitHub")
        )
          .withHref("https://github.com/teggr/ci-ready-maven")
          .withTarget("_blank")
          .withRel("noopener noreferrer")
          .withClass("github-cta")
      ).withClass("hero-left"),
      // Right column - Description paragraphs
      div(
        p("A catalogue of Maven plugins and utilities to help Java developers build robust CI pipelines and quality gates. Discover the tools that keep enterprise codebases clean, tested, secure, and maintainable."),
        p("Each tool is documented with its Maven coordinates, category, a concise description, and a ready-to-use configuration example. Whether you are setting up a new project or tightening an existing pipeline, find the right plugin for every layer of your quality stack.")
      ).withClass("hero-right")
    ).withClass("hero-section"),
    // Sort controls section
    div(
      div(
        button(
          i().withClass("bi bi-sort-alpha-down"),
          text(" Alphabetical")
        )
          .withClass("sort-btn active")
          .attr("hx-get", "grid-alphabetical.html")
          .attr("hx-target", "#browse-section")
          .attr("hx-swap", "outerHTML transition:true"),
        button(
          i().withClass("bi bi-tags"),
          text(" Category")
        )
          .withClass("sort-btn")
          .attr("hx-get", "grid-by-tag.html")
          .attr("hx-target", "#browse-section")
          .attr("hx-swap", "outerHTML transition:true"),
        button(
          i().withClass("bi bi-clock-history"),
          text(" Recently Added")
        )
          .withClass("sort-btn")
          .attr("hx-get", "grid-by-date.html")
          .attr("hx-target", "#browse-section")
          .attr("hx-swap", "outerHTML transition:true")
      ).withClass("sort-button-group").withId("sort-buttons")
    ).withClass("sort-controls-section"),
    gridAlphabeticalContent(markdownData),
    div(
      hr().withClass("tag-separator"),
      div(
        each(uniqueTags, tag -> {
          String tagSlug = tagToSlug(tag);
          String tagFileName = "tag-" + tagSlug + ".html";
          return a(tag)
            .withHref(tagFileName)
            .attr("hx-get", tagFileName)
            .attr("hx-target", "body")
            .attr("hx-swap", "innerHTML transition:true show:window:top")
            .attr("hx-push-url", "true")
            .withClass("tag-cloud-item");
        })
      ).withClass("tag-cloud")
    ).withClass("tag-cloud-section").withId("tags-section"),
    div(
      hr().withClass("about-separator"),
      div(
        h2("About This Catalogue"),
        p("Enterprise Java development depends on reliable, automated quality gates. Formatting, static analysis, test coverage, dependency governance, security scanning, and license checks should all run the same way for every developer, every AI agent, and every CI pipeline."),
        p("This catalogue was inspired by the article " +
          "<a href=\"https://foojay.io/today/enterprise-java-quality-gates-ai/\" target=\"_blank\" rel=\"noopener noreferrer\">Enterprise Java Quality Gates &amp; AI</a>" +
          " on Foojay.io, which describes how deterministic tooling protects codebases as teams and AI agents grow."),
        p(
          text("This is a "),
          strong("community-driven reference"),
          text(". If you know a Maven plugin that belongs here, contribute on GitHub!")
        )
      ).withClass("about-content")
    ).withClass("about-section").withId("about-section")
  ).withId("main-content");
}

// SEO metadata record for page generation
record SeoMetadata(String title, String description, String url, List<String> tags) {
  static SeoMetadata index() {
    return new SeoMetadata(
      "CI Ready Maven - Maven Plugins for CI Pipelines and Quality Gates",
      "A catalogue of Maven plugins and utilities for building robust CI pipelines and quality gates in enterprise Java projects.",
      "",
      List.of("Maven", "CI", "Quality Gates", "Java", "DevOps")
    );
  }

  static SeoMetadata forTag(String tag) {
    return new SeoMetadata(
      tag + " - CI Ready Maven",
      "Maven plugins and tools for " + tag.toLowerCase() + " in Java CI pipelines and quality gates.",
      "tag-" + tagToSlug(tag) + ".html",
      List.of("Maven", "CI", tag)
    );
  }

  static SeoMetadata forTool(Map<String, List<String>> data, String htmlFileName) {
    String name = data.getOrDefault("name", List.of("Unknown Tool")).get(0);
    List<String> toolTags = data.getOrDefault("tags", List.of());
    String category = data.getOrDefault("category", List.of("")).get(0);
    String type = data.getOrDefault("type", List.of("")).get(0);

    String description = name + " - " + category + ". ";
    if (!type.isEmpty()) {
      description += type + ". ";
    }
    if (!toolTags.isEmpty()) {
      description += "Categories: " + String.join(", ", toolTags) + ".";
    }

    return new SeoMetadata(
      name + " - CI Ready Maven",
      description,
      htmlFileName,
      toolTags
    );
  }

  private static String tagToSlug(String tag) {
    return tag.toLowerCase()
      .replaceAll("\\s+", "-")
      .replaceAll("[^a-z0-9-]", "");
  }
}

static final String SITE_BASE_URL = "https://teggr.github.io/ci-ready-maven/";

static HtmlTag output(DomContent content, SeoMetadata seo) {
  String fullImageUrl = SITE_BASE_URL + "maven-logo.png";
  String fullPageUrl = SITE_BASE_URL + seo.url();
  String keywords = String.join(", ", seo.tags());

  return html(
    head(
      meta().withCharset("UTF-8"),
      meta().withName("viewport").withContent("width=device-width, initial-scale=1.0"),

      // Basic SEO meta tags
      title(seo.title()),
      meta().withName("description").withContent(seo.description()),
      meta().withName("keywords").withContent(keywords),
      meta().withName("author").withContent("CI Ready Maven Community"),
      meta().withName("robots").withContent("index, follow"),

      // Open Graph meta tags
      meta().attr("property", "og:type").withContent("website"),
      meta().attr("property", "og:title").withContent(seo.title()),
      meta().attr("property", "og:description").withContent(seo.description()),
      meta().attr("property", "og:image").withContent(fullImageUrl),
      meta().attr("property", "og:url").withContent(fullPageUrl),
      meta().attr("property", "og:site_name").withContent("CI Ready Maven"),
      meta().attr("property", "og:locale").withContent("en_US"),

      // Twitter Card meta tags
      meta().withName("twitter:card").withContent("summary_large_image"),
      meta().withName("twitter:title").withContent(seo.title()),
      meta().withName("twitter:description").withContent(seo.description()),
      meta().withName("twitter:image").withContent(fullImageUrl),

      // Canonical URL
      link().withRel("canonical").withHref(fullPageUrl),

      // Fonts and stylesheets
      link().withRel("preconnect").withHref("https://fonts.googleapis.com"),
      link().withRel("preconnect").withHref("https://fonts.gstatic.com").attr("crossorigin", ""),
      link().withRel("stylesheet").withHref("https://fonts.googleapis.com/css2?family=Poppins:wght@700;800&display=swap"),
      link().withRel("stylesheet").withHref("css/styles.css"),
      link().withRel("stylesheet").withHref("https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css"),
      script().withSrc("https://unpkg.com/htmx.org@2.0.4")
    ),
    body(
      content,
      siteFooter()
    )
  ).attr("lang", "en");
}

private static DomContent siteFooter() {
  return footer(
    div(
      div(
        text("Built with "),
        a(
          i().withClass("bi bi-hammer"),
          text(" j2html")
        )
          .withHref("https://j2html.com/")
          .withTarget("_blank")
          .withRel("noopener noreferrer")
          .withClass("footer-link"),
        text(", "),
        a(
          i().withClass("bi bi-lightning-charge"),
          text(" htmx")
        )
          .withHref("https://htmx.org/")
          .withTarget("_blank")
          .withRel("noopener noreferrer")
          .withClass("footer-link"),
        text(" & "),
        a(
          i().withClass("bi bi-terminal"),
          text(" JBang")
        )
          .withHref("https://www.jbang.dev/")
          .withTarget("_blank")
          .withRel("noopener noreferrer")
          .withClass("footer-link")
      ).withClass("footer-content")
    ).withClass("footer-container")
  ).withClass("site-footer");
}
