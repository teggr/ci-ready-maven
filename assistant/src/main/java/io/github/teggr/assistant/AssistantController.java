package io.github.teggr.assistant;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class AssistantController {
    private final AssistantService service;
    private final AssistantProperties properties;

    public AssistantController(AssistantService service, AssistantProperties properties) {
        this.service = service;
        this.properties = properties;
    }

    @GetMapping(value = "/assistant/widget", produces = MediaType.TEXT_HTML_VALUE)
    String widget(Model model) {
        model.addAttribute("enabled", properties.enabled());
        model.addAttribute("maxQuestionLength", properties.maxQuestionLength());
        return "assistant/widget";
    }

    @PostMapping(value = "/assistant/chat", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.TEXT_HTML_VALUE)
    String chat(@RequestBody(required = false) String body, Model model) {
        String question = parseQuestion(body);
        AssistantResponse response;
        if (question == null || question.isBlank() || question.length() > properties.maxQuestionLength()
                || question.codePoints().anyMatch(c -> Character.isISOControl(c) && c != '\n' && c != '\t')) {
            response = AssistantResponse.message("", "Please enter a question of at most "
                    + properties.maxQuestionLength() + " characters.");
        } else {
            response = service.answer(question.strip());
        }
        model.addAttribute("response", response);
        return "assistant/fragments :: response";
    }

    private static String parseQuestion(String body) {
        if (body == null) return null;
        String question = null;
        try {
            for (String field : body.split("&")) {
                String[] pair = field.split("=", 2);
                if ("question".equals(URLDecoder.decode(pair[0], StandardCharsets.UTF_8))) {
                    if (question != null) return null;
                    question = pair.length == 2 ? URLDecoder.decode(pair[1], StandardCharsets.UTF_8) : "";
                }
            }
        } catch (IllegalArgumentException ex) {
            return null;
        }
        return question;
    }
}
