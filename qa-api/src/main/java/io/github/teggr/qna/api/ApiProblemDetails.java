package io.github.teggr.qna.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public final class ApiProblemDetails {
    private ApiProblemDetails() {}

    public static ProblemDetail create(HttpStatus status, String code, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setType(URI.create("about:blank"));
        problem.setTitle(status.getReasonPhrase());
        problem.setProperty("code", code);
        return problem;
    }

    public static void write(ObjectMapper objectMapper, OutputStream output, ProblemDetail problem)
            throws IOException {
        ObjectNode json = objectMapper.createObjectNode();
        json.put("type", problem.getType().toString());
        json.put("title", problem.getTitle());
        json.put("status", problem.getStatus());
        json.put("detail", problem.getDetail());
        if (problem.getInstance() != null) {
            json.put("instance", problem.getInstance().toString());
        }
        if (problem.getProperties() != null) {
            problem.getProperties().forEach((name, value) -> json.set(name, objectMapper.valueToTree(value)));
        }
        objectMapper.writeValue(output, json);
    }
}
