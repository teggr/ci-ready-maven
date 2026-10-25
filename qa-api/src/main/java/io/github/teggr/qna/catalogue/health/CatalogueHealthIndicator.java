package io.github.teggr.qna.catalogue.health;

import io.github.teggr.qna.api.ApiProperties;
import io.github.teggr.qna.catalogue.CatalogueIndex;
import io.github.teggr.qna.assistant.QaProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.boot.health.contributor.Status;
import org.springframework.stereotype.Component;

@Component("qna")
public class CatalogueHealthIndicator implements HealthIndicator {
    private final CatalogueIndex catalogueIndex;
    private final QaProperties properties;
    private final ApiProperties apiProperties;
    private final String apiKey;

    public CatalogueHealthIndicator(
            CatalogueIndex catalogueIndex,
            QaProperties properties,
            ApiProperties apiProperties,
            @Value("${spring.ai.openai.api-key:}") String apiKey) {
        this.catalogueIndex = catalogueIndex;
        this.properties = properties;
        this.apiProperties = apiProperties;
        this.apiKey = apiKey;
    }

    @Override
    public Health health() {
        boolean modelConfigured = apiKey != null && !apiKey.isBlank();
        return Health.status(modelConfigured ? Status.UP : Status.OUT_OF_SERVICE)
                .withDetail("catalogueRecords", catalogueIndex.records().size())
                .withDetail("modelConfigured", modelConfigured)
                .withDetail("providerAvailability", "not checked by health probes")
                .withDetail("rateLimit", apiProperties.getRequestsPerWindow() + " requests per configured window")
                .build();
    }
}
