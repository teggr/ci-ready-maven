package io.github.teggr.qna.api;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import io.github.teggr.qna.assistant.AnswerStatus;
import io.github.teggr.qna.assistant.AssistantException;
import io.github.teggr.qna.assistant.AssistantService;
import io.github.teggr.qna.assistant.ChatRequest;
import io.github.teggr.qna.assistant.ChatResponse;
import io.github.teggr.qna.assistant.InvalidQuestionException;
import io.github.teggr.qna.assistant.SourceDto;
import io.github.teggr.qna.api.security.RequestBodyLimitFilter;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
        properties = {
            "spring.ai.openai.api-key=test-key",
            "qa.assistant.max-question-length=10",
            "qa.api.max-request-body-bytes=2048",
            "qa.api.requests-per-window=100"
        })
@AutoConfigureMockMvc
class ChatApiIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AssistantService assistantService;

    @Test
    void startsWithPackagedCatalogueAndReturnsChatContract() throws Exception {
        when(assistantService.answer(new ChatRequest("spotbugs"))).thenReturn(new ChatResponse(
                AnswerStatus.ANSWERED,
                "SpotBugs performs static analysis.",
                List.of(new SourceDto(
                        "spotbugs-maven-plugin",
                        "SpotBugs Maven Plugin",
                        "https://teggr.github.io/ci-ready-maven/plugins/spotbugs-maven-plugin.html"))));

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\":\"spotbugs\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ANSWERED"))
                .andExpect(jsonPath("$.sources[0].id").value("spotbugs-maven-plugin"))
                .andExpect(jsonPath("$.sources[0].catalogueUrl")
                        .value("https://teggr.github.io/ci-ready-maven/plugins/spotbugs-maven-plugin.html"));
    }

    @Test
    void rejectsInvalidAndOversizedRequests() throws Exception {
        doThrow(new ConstraintViolationException("answer.request.question: The question must not be empty.",
                        java.util.Set.of()))
                .when(assistantService)
                .answer(new ChatRequest("   "));

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\":\"   \"}"))
                .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                        .andExpect(jsonPath("$.status").value(400))
                        .andExpect(jsonPath("$.title").value("Bad Request"))
                        .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        doThrow(
                        new InvalidQuestionException("The question exceeds the configured character limit."))
                .when(assistantService)
                .answer(new ChatRequest("question is too long"));

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\":\"question is too long\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.detail").value("The question exceeds the configured character limit."));
    }

    @Test
    void mapsAssistantFailuresToApiErrors() throws Exception {
        doThrow(new AssistantException(AssistantException.Reason.MODEL_TIMEOUT))
                .when(assistantService)
                .answer(new ChatRequest("timeout"));

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\":\"timeout\"}"))
                .andExpect(status().isGatewayTimeout())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(504))
                .andExpect(jsonPath("$.code").value("MODEL_TIMEOUT"));
    }

    @Test
    void rejectsOversizedRequestBodies() throws Exception {
        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("x".repeat(2049)))
                .andExpect(status().isPayloadTooLarge())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(413))
                .andExpect(jsonPath("$.code").value("REQUEST_TOO_LARGE"));
    }

    @Test
    void allowsOnlyConfiguredStaticSiteOrigin() throws Exception {
        mockMvc.perform(options("/api/v1/chat")
                        .header("Origin", "https://teggr.github.io")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://teggr.github.io"));

        mockMvc.perform(options("/api/v1/chat")
                        .header("Origin", "https://attacker.example")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    @Test
    void healthReportsIndexAndModelConfigurationWithoutProviderCall() throws Exception {
        mockMvc.perform(get("/actuator/health/qna"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.details.catalogueRecords").isNumber())
                .andExpect(jsonPath("$.details.modelConfigured").value(true));
    }
}
