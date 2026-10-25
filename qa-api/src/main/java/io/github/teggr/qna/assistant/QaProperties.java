package io.github.teggr.qna.assistant;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.DecimalMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "qa.assistant")
public class QaProperties {
    @Min(1)
    @Max(5000)
    private int maxQuestionLength = 1000;

    @Min(1)
    @Max(10)
    private int retrievalLimit = 5;

    @DecimalMin("1.0")
    private double minimumEvidenceScore = 3.5;

    @Min(1000)
    private int maxContextCharacters = 12000;

    @Min(100)
    @Max(12000)
    private int maxAnswerCharacters = 4000;

    @Min(1)
    @Max(180)
    private int modelTimeoutSeconds = 30;

    @Min(1)
    @Max(64)
    private int maxConcurrentModelCalls = 4;

    @Min(1)
    @Max(1000)
    private int modelQueueCapacity = 8;

    public int getMaxQuestionLength() {
        return maxQuestionLength;
    }

    public void setMaxQuestionLength(int maxQuestionLength) {
        this.maxQuestionLength = maxQuestionLength;
    }

    public int getRetrievalLimit() {
        return retrievalLimit;
    }

    public void setRetrievalLimit(int retrievalLimit) {
        this.retrievalLimit = retrievalLimit;
    }

    public double getMinimumEvidenceScore() {
        return minimumEvidenceScore;
    }

    public void setMinimumEvidenceScore(double minimumEvidenceScore) {
        this.minimumEvidenceScore = minimumEvidenceScore;
    }

    public int getMaxContextCharacters() {
        return maxContextCharacters;
    }

    public void setMaxContextCharacters(int maxContextCharacters) {
        this.maxContextCharacters = maxContextCharacters;
    }

    public int getMaxAnswerCharacters() {
        return maxAnswerCharacters;
    }

    public void setMaxAnswerCharacters(int maxAnswerCharacters) {
        this.maxAnswerCharacters = maxAnswerCharacters;
    }

    public int getModelTimeoutSeconds() {
        return modelTimeoutSeconds;
    }

    public void setModelTimeoutSeconds(int modelTimeoutSeconds) {
        this.modelTimeoutSeconds = modelTimeoutSeconds;
    }

    public int getMaxConcurrentModelCalls() {
        return maxConcurrentModelCalls;
    }

    public void setMaxConcurrentModelCalls(int maxConcurrentModelCalls) {
        this.maxConcurrentModelCalls = maxConcurrentModelCalls;
    }

    public int getModelQueueCapacity() {
        return modelQueueCapacity;
    }

    public void setModelQueueCapacity(int modelQueueCapacity) {
        this.modelQueueCapacity = modelQueueCapacity;
    }

}
