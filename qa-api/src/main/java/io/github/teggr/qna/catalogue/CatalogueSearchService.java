package io.github.teggr.qna.catalogue;

import io.github.teggr.qna.assistant.QaProperties;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class CatalogueSearchService {
    private static final Pattern WORDS = Pattern.compile("[\\p{L}\\p{N}]+");
    private static final Set<String> STOP_WORDS =
            Set.of(
                    "a",
                    "an",
                    "and",
                    "are",
                    "can",
                    "for",
                    "how",
                    "i",
                    "in",
                    "is",
                    "me",
                    "maven",
                    "of",
                    "plugin",
                    "plugins",
                    "the",
                    "to",
                    "tool",
                    "tools",
                    "what",
                    "which",
                    "with");
    private static final Set<String> COMPARISON_WORDS = Set.of("compare", "comparison", "versus", "vs", "difference", "differences");

    private final CatalogueIndex index;
    private final QaProperties properties;

    public CatalogueSearchService(CatalogueIndex index, QaProperties properties) {
        this.index = index;
        this.properties = properties;
    }

    public List<SearchResult> search(String question) {
        String query = question.toLowerCase(Locale.ROOT).strip();
        Set<String> queryTerms = terms(query);
        if (queryTerms.isEmpty()) {
            return List.of();
        }

        List<SearchResult> matches = new ArrayList<>();
        for (CatalogueRecord record : index.records()) {
            double score = score(query, queryTerms, record);
            if (score > 0) {
                matches.add(new SearchResult(record, score));
            }
        }
        return matches.stream()
                .sorted(Comparator.comparingDouble(SearchResult::score)
                        .reversed()
                        .thenComparing(result -> result.record().id()))
                .limit(properties.getRetrievalLimit())
                .toList();
    }

    public boolean hasSufficientEvidence(String question, List<SearchResult> results) {
        if (results.isEmpty() || results.getFirst().score() < properties.getMinimumEvidenceScore()) {
            return false;
        }
        Set<String> queryTerms = terms(question.toLowerCase(Locale.ROOT));
        boolean comparison = queryTerms.stream().anyMatch(COMPARISON_WORDS::contains);
        return !comparison || results.stream()
                .filter(result -> result.score() >= properties.getMinimumEvidenceScore())
                .count() >= 2;
    }

    private double score(String query, Set<String> queryTerms, CatalogueRecord record) {
        String title = lower(record.title());
        String coordinates = lower(record.mavenCoordinates());
        String category = lower(record.category());
        if (query.equals(title) || query.equals(record.id())) {
            return 100;
        }
        if (!coordinates.isBlank() && query.contains(coordinates)) {
            return 100;
        }

        double score = 0;
        if (title.contains(query) || record.id().contains(query)) {
            score += 60;
        }
        if (!coordinates.isBlank() && coordinates.contains(query)) {
            score += 40;
        }

        Set<String> titleTerms = terms(title + " " + record.id());
        Set<String> categoryTerms = terms(category);
        Set<String> tagTerms = terms(String.join(" ", record.tags()));
        Set<String> coordinateTerms = terms(coordinates);
        Set<String> contentTerms = terms(
                lower(record.description()) + " " + lower(record.content()) + " " + lower(record.codeExample()));

        for (String term : queryTerms) {
            if (coordinateTerms.contains(term) && !STOP_WORDS.contains(term)) {
                score += 10;
            } else if (titleTerms.contains(term)) {
                score += 8;
            } else if (categoryTerms.contains(term)) {
                score += 7;
            } else if (tagTerms.contains(term)) {
                score += 6;
            } else if (contentTerms.contains(term)) {
                score += 2.5;
            }
        }
        return score;
    }

    private Set<String> terms(String text) {
        Set<String> result = new HashSet<>();
        var matcher = WORDS.matcher(text);
        while (matcher.find()) {
            String term = matcher.group().toLowerCase(Locale.ROOT);
            if (term.length() > 1 && !STOP_WORDS.contains(term)) {
                result.add(term);
            }
        }
        return result;
    }

    private String lower(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }
}
