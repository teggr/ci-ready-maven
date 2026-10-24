package io.github.teggr.assistant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "ci-ready-maven.assistant.enabled=true",
        "spring.ai.model.chat=none"
})
@AutoConfigureMockMvc
class AssistantMetadataWebTests {
    @Autowired MockMvc mvc;
    @org.springframework.boot.test.web.server.LocalServerPort int port;

    @Test
    void realServletFormBodySurvivesByteLimitFilter() throws Exception {
        try (var client = java.net.http.HttpClient.newHttpClient()) {
            var request = java.net.http.HttpRequest.newBuilder(
                            java.net.URI.create("http://127.0.0.1:" + port + "/assistant/chat"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(
                            "question=What+are+the+Maven+coordinates+for+JaCoCo%3F")).build();
            var response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
            org.assertj.core.api.Assertions.assertThat(response.statusCode()).isEqualTo(200);
            org.assertj.core.api.Assertions.assertThat(response.body()).contains("org.jacoco:jacoco-maven-plugin");
        }
    }

    @Test
    void exactCatalogueMetadataWorksWithoutAnyModelOrCredentials() throws Exception {
        for (String question : new String[]{
                "What are the Maven coordinates for JaCoCo?",
                "What category and tags does JaCoCo have?",
                "What version of JaCoCo is in the catalogue?"}) {
            mvc.perform(post("/assistant/chat").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .content("question=" + java.net.URLEncoder.encode(question, java.nio.charset.StandardCharsets.UTF_8)))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("org.jacoco:jacoco-maven-plugin")))
                    .andExpect(content().string(containsString("Source commit:")))
                    .andExpect(content().string(containsString("Content SHA-256:")))
                    .andExpect(content().string(containsString("may be stale")));
        }
    }
}
