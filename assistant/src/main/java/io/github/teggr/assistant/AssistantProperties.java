package io.github.teggr.assistant;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("ci-ready-maven.assistant")
public record AssistantProperties(
        @DefaultValue("false") boolean enabled,
        @DefaultValue("1000") @Min(20) @Max(4000) int maxQuestionLength,
        @DefaultValue("8192") @Min(1024) @Max(32768) int maxRequestBytes,
        @DefaultValue("5") @Min(1) @Max(10) int maxResults,
        @DefaultValue("12000") @Min(1000) @Max(30000) int maxContextCharacters,
        @DefaultValue("4000") @Min(500) @Max(8000) int maxRecordCharacters,
        @DefaultValue("6") @Min(1) @Max(10) int maxSelections,
        @DefaultValue("1200") @Min(100) @Max(2000) int maxExcerptCharacters,
        @DefaultValue("16000") @Min(1000) @Max(24000) int maxOutputCharacters,
        @DefaultValue("600") @Min(64) @Max(2400) int maxAnswerTokens,
        @DefaultValue("2") @Min(1) @Max(8) int maxConcurrentRequests,
        @DefaultValue("10") @Min(1) @Max(60) int requestsPerWindow,
        @DefaultValue("60") @Min(10) @Max(3600) int rateWindowSeconds,
        @DefaultValue("2048") @Min(16) @Max(10000) int maxRateLimitClients,
        @DefaultValue("3000") @Min(100) @Max(10000) int connectTimeoutMillis,
        @DefaultValue("20000") @Min(1000) @Max(60000) int readTimeoutMillis) {
}
