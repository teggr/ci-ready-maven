package io.github.teggr.qna.api;

import jakarta.validation.ConstraintViolationException;
import io.github.teggr.qna.assistant.AssistantException;
import io.github.teggr.qna.assistant.InvalidQuestionException;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(AssistantException.class)
    public ResponseEntity<ProblemDetail> handleAssistantException(AssistantException exception) {
        return switch (exception.reason()) {
            case MODEL_NOT_CONFIGURED -> error(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "MODEL_UNAVAILABLE",
                    "The answer service is not configured.");
            case CAPACITY_LIMITED -> error(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "CAPACITY_LIMITED",
                    "The answer service is busy. Please try again later.");
            case MODEL_TIMEOUT -> error(
                    HttpStatus.GATEWAY_TIMEOUT,
                    "MODEL_TIMEOUT",
                    "The answer service timed out. Please try again.");
            case MODEL_INTERRUPTED -> error(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "MODEL_UNAVAILABLE",
                    "The answer service is temporarily unavailable.");
            case MODEL_ERROR -> error(
                    HttpStatus.BAD_GATEWAY,
                    "MODEL_ERROR",
                    "The answer service could not produce a response.");
            case INVALID_MODEL_RESPONSE -> error(
                    HttpStatus.BAD_GATEWAY,
                    "INVALID_MODEL_RESPONSE",
                    "The answer service returned an invalid response.");
        };
    }

    @ExceptionHandler(InvalidQuestionException.class)
    public ResponseEntity<ProblemDetail> handleInvalidQuestion(InvalidQuestionException exception) {
        return error(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", exception.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public ResponseEntity<ProblemDetail> handleValidationException(Exception exception) {
        String message = "The question must not be empty.";
        if (exception instanceof MethodArgumentNotValidException validationException
                && !validationException.getBindingResult().getFieldErrors().isEmpty()) {
            message = Objects.requireNonNullElse(
                    validationException.getBindingResult().getFieldErrors().getFirst().getDefaultMessage(), message);
        }
        return error(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleMalformedJson() {
        return error(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "The request body must be valid JSON.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpectedException(Exception exception) {
        LOGGER.warn("Request failed with {}", exception.getClass().getSimpleName());
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "The request could not be completed.");
    }

    private ResponseEntity<ProblemDetail> error(HttpStatus status, String code, String detail) {
        ProblemDetail problem = ApiProblemDetails.create(status, code, detail);
        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_PROBLEM_JSON).body(problem);
    }
}
