package io.github.teggr.qna.api;

import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.teggr.qna.assistant.AssistantService;
import io.github.teggr.qna.assistant.ChatRequest;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.ai.openai.api-key=test-key")
class AssistantServiceValidationTest {
    @Autowired
    private AssistantService assistantService;

    @Test
    void validatesChatRequestBeforeExecutingServiceMethod() {
        assertThrows(
                ConstraintViolationException.class,
                () -> assistantService.answer(new ChatRequest("   ")));
    }
}
