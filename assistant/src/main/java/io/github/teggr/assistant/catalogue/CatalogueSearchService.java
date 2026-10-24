package io.github.teggr.assistant.catalogue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public final class CatalogueSearchService {
    private static final Set<String> GENERIC = Set.of(
            "a", "an", "the", "and", "or", "for", "of", "to", "in", "on", "with", "from", "by",
            "is", "are", "be", "it", "its", "this", "that", "what", "which", "how", "why", "when",
            "i", "my", "me", "we", "our", "you", "your", "please", "tell", "about", "show", "give",
            "use", "using", "used", "maven", "plugin", "plugins", "extension", "extensions", "tool", "tools",
            "java", "ci", "build", "builds", "project", "projects", "pipeline", "pipelines",
            "recommend", "find", "need", "want", "help", "available", "catalogue", "catalog",
            "configuration", "configure", "example", "examples", "check", "checks",
            "documented", "relevant", "related",
            "does", "do", "can", "could", "would", "should", "has", "have", "support", "supports",
            "compare", "comparison", "versus", "vs", "between", "difference", "differences", "better",
            "version", "versions", "release", "released", "listed", "recorded", "last");
    private static final Set<String> BROAD_ALIASES = Set.of(
            "dependency", "versions", "release", "source", "compiler", "exec", "license",
            "assembly", "artifact", "deploy", "coverage", "format", "testing");
    private static final Pattern COMPARISON = Pattern.compile("\\b(compare|comparison|versus|vs|difference|differences|better)\\b");
    private static final Pattern LIVE_VERSION = Pattern.compile("\\b(latest|current|today|newest)\\b");
    private static final Pattern RECORDED_VERSION = Pattern.compile("\\b(catalogue|catalog|listed|recorded)\\b");
    private static final Pattern VERSION = Pattern.compile("\\b(version|versions|release|released)\\b");
    private final List<Entry> entries;

    public CatalogueSearchService(CatalogueRepository repository) {
        entries = repository.records().stream().map(Entry::new).toList();
    }

    public record Match(CatalogueRecord record, double score, boolean exact) {}

    public Set<String> namedRecordIds(String question) {
        if (question == null || question.isBlank()) return Set.of();
        String normalized = normalize(question);
        return entries.stream().filter(entry -> entry.named(normalized)).map(entry -> entry.record.id())
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
    }

    public List<Match> search(String question, int limit) {
        if (question == null || question.isBlank() || limit <= 0) return List.of();
        String normalized = normalize(question);
        Set<String> terms = terms(question);
        List<Match> matches = new ArrayList<>();
        for (Entry entry : entries) {
            boolean exact = entry.named(normalized);
            double score = exact ? 100 : 0;
            for (String term : terms) {
                if (entry.heading.contains(term)) score += 12;
                if (entry.facets.contains(term)) score += 8;
                if (entry.description.contains(term)) score += 5;
                if (entry.body.contains(term)) score += 1;
            }
            if (score > 0) matches.add(new Match(entry.record, score, exact));
        }
        return matches.stream().sorted(Comparator.comparingDouble(Match::score).reversed()
                .thenComparing(match -> match.record().id())).limit(Math.min(limit, 20)).toList();
    }

    public boolean sufficient(String question, List<Match> matches) {
        if (question == null || question.isBlank() || matches == null || matches.isEmpty()) return false;
        String normalized = normalize(question);
        List<Entry> named = entries.stream().filter(entry -> entry.named(normalized)).toList();
        Set<String> supplied = new HashSet<>();
        for (Match match : matches) {
            if (match == null || match.record() == null || !Double.isFinite(match.score())) return false;
            supplied.add(match.record().id());
        }
        if (named.stream().anyMatch(entry -> !supplied.contains(entry.record.id()))) return false;
        if (LIVE_VERSION.matcher(normalized).find()
                && !RECORDED_VERSION.matcher(normalized).find()) return false;

        String intent = normalized;
        for (Entry entry : named) {
            for (String alias : entry.aliases.stream().sorted(Comparator.comparingInt(String::length).reversed()).toList()) {
                intent = (" " + intent + " ").replace(" " + alias + " ", " ").strip();
            }
        }
        Set<String> requested = terms(intent);
        if (COMPARISON.matcher(normalized).find() && named.size() < 2) {
            if (!named.isEmpty() || !categoryComparison(normalized, requested, matches)) return false;
        }
        boolean version = VERSION.matcher(normalized).find();
        if (!named.isEmpty()) {
            for (Entry entry : named) {
                if (version && (!(entry.record.metadata().get("lastRelease") instanceof String value) || value.isBlank())) return false;
                // A name alone is evidence of identity, not evidence of an absent capability.
                if (!entry.all.containsAll(requested)) return false;
            }
            return true;
        }
        if (requested.isEmpty() || version) return false;
        Match best = matches.getFirst();
        Entry entry = entries.stream().filter(candidate -> candidate.record.id().equals(best.record().id())).findFirst().orElse(null);
        if (entry == null || best.score() < 5) return false;
        long covered = requested.stream().filter(entry.all::contains).count();
        return covered == requested.size() && requested.stream().anyMatch(term ->
                entry.facets.contains(term) || entry.description.contains(term) || entry.heading.contains(term));
    }

    private boolean categoryComparison(String question, Set<String> requested, List<Match> matches) {
        if (requested.isEmpty() || matches.size() < 2) return false;
        String padded = " " + question + " ";
        return matches.stream().allMatch(match -> entries.stream().anyMatch(entry ->
                entry.record.id().equals(match.record().id()) && entry.facets.containsAll(requested)
                        && entry.facetPhrases.stream().anyMatch(phrase -> padded.contains(" " + phrase + " "))));
    }

    private static String normalize(String text) {
        return text.toLowerCase(Locale.ROOT).replaceAll("['’]s\\b", "")
                .replaceAll("[^\\p{L}\\p{N}]+", " ").strip().replaceAll("\\s+", " ");
    }

    private static Set<String> tokens(String text) {
        Set<String> result = new LinkedHashSet<>();
        for (String token : normalize(text).split(" ")) {
            if (!token.isBlank()) {
                token = switch (token) {
                    case "measuring", "measurement", "measurements", "measures" -> "measure";
                    case "enforcing", "enforcement", "enforces" -> "enforce";
                    case "coordinate", "coordinates" -> "mavencoordinates";
                    default -> token;
                };
                result.add(token);
                if (token.length() > 4 && token.endsWith("s")) result.add(token.substring(0, token.length() - 1));
            }
        }
        return result;
    }

    private static Set<String> terms(String text) {
        Set<String> result = tokens(text);
        result.removeAll(GENERIC);
        return result;
    }

    private static final class Entry {
        private final CatalogueRecord record;
        private final Set<String> aliases = new LinkedHashSet<>();
        private final Set<String> heading;
        private final Set<String> facets;
        private final Set<String> facetPhrases = new HashSet<>();
        private final Set<String> description;
        private final Set<String> body;
        private final Set<String> all = new HashSet<>();

        private Entry(CatalogueRecord record) {
            this.record = record;
            String slug = record.id().substring(record.id().indexOf('/') + 1);
            addAlias(slug);
            addAlias(record.title());
            if (slug.equals("pitest-maven")) addAlias("PIT");
            if (slug.equals("owasp-dependency-check")) addAlias("Dependency Check");
            Map<String, Object> meta = record.metadata();
            for (String key : List.of("name", "mavenCoordinates")) {
                if (meta.get(key) instanceof String text) {
                    addAlias(text);
                    if (key.equals("mavenCoordinates") && text.contains(":")) addAlias(text.substring(text.indexOf(':') + 1));
                }
            }
            for (String alias : List.copyOf(aliases)) {
                String reduced = alias.replaceAll("\\b(maven|plugin|extension)\\b", "").strip().replaceAll("\\s+", " ");
                if (!reduced.isBlank() && !BROAD_ALIASES.contains(reduced)) aliases.add(reduced);
            }
            heading = tokens(record.title() + " " + slug + " " + meta.getOrDefault("name", ""));
            facets = tokens(meta.getOrDefault("category", "") + " " + meta.getOrDefault("tags", ""));
            if (meta.get("category") instanceof String category) facetPhrases.add(normalize(category));
            if (meta.get("tags") instanceof List<?> tags) {
                tags.stream().filter(String.class::isInstance).map(String.class::cast)
                        .map(CatalogueSearchService::normalize).forEach(facetPhrases::add);
            }
            description = tokens(String.valueOf(meta.getOrDefault("description", "")));
            body = tokens(record.body());
            all.addAll(tokens(meta.toString()));
            all.addAll(heading);
            all.addAll(body);
        }

        private void addAlias(String alias) {
            String normalized = normalize(alias);
            if (!normalized.isBlank() && !BROAD_ALIASES.contains(normalized)) aliases.add(normalized);
        }

        private boolean named(String question) {
            String padded = " " + question + " ";
            return aliases.stream().anyMatch(alias -> padded.contains(" " + alias + " "));
        }
    }
}
