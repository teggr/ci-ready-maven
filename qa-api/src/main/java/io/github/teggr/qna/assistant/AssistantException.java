package io.github.teggr.qna.assistant;

public class AssistantException extends RuntimeException {
    private final Reason reason;

    public AssistantException(Reason reason) {
        super(reason.name());
        this.reason = reason;
    }

    public Reason reason() {
        return reason;
    }

    public enum Reason {
        MODEL_NOT_CONFIGURED,
        CAPACITY_LIMITED,
        MODEL_TIMEOUT,
        MODEL_INTERRUPTED,
        MODEL_ERROR,
        INVALID_MODEL_RESPONSE
    }
}
