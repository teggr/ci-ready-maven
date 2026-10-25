package io.github.teggr.qna.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class PackagedRuntimeSmokeIT {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void packagedApplicationStartsWithoutCatalogueSourceOrModelCredentials() throws Exception {
        Path jar = Path.of(System.getProperty("qna.application.jar")).toAbsolutePath();
        assertTrue(Files.isRegularFile(jar), "The repackaged application JAR must exist.");

        Path isolatedDirectory = Files.createTempDirectory("qna-api-smoke-");
        assertFalse(Files.exists(isolatedDirectory.resolve("static-site")));
        int port = availablePort();
        Path nonexistentSource = isolatedDirectory.resolve("catalogue-source-does-not-exist");
        String java = Path.of(System.getProperty("java.home"), "bin", executableName()).toString();
        Process process = new ProcessBuilder(List.of(
                        java,
                        "-jar",
                        jar.toString(),
                        "--server.port=" + port,
                        "--spring.ai.openai.api-key=",
                        "--catalogue.source.dir=" + nonexistentSource))
                .directory(isolatedDirectory.toFile())
                .redirectErrorStream(true)
                .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                .start();

        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(1))
                    .build();
            HttpResponse<String> health = awaitHealth(client, port, process);
            assertEquals(503, health.statusCode());
            JsonNode healthJson = objectMapper.readTree(health.body());
            JsonNode qna = healthJson.path("components").path("qna");
            assertEquals("OUT_OF_SERVICE", qna.path("status").asText());
            assertTrue(qna.path("details").path("catalogueRecords").asInt() > 0);
            assertFalse(qna.path("details").path("modelConfigured").asBoolean());

            HttpRequest chatRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + port + "/api/v1/chat"))
                    .timeout(Duration.ofSeconds(3))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"question\":\"google-java-format\"}"))
                    .build();
            HttpResponse<String> chat = client.send(chatRequest, HttpResponse.BodyHandlers.ofString());
            assertEquals(503, chat.statusCode());
            assertEquals("MODEL_UNAVAILABLE", objectMapper.readTree(chat.body()).path("code").asText());
        } finally {
            process.destroy();
            if (!process.waitFor(3, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                process.waitFor(3, TimeUnit.SECONDS);
            }
            Files.deleteIfExists(isolatedDirectory);
        }
    }

    private HttpResponse<String> awaitHealth(HttpClient client, int port, Process process) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/actuator/health"))
                .timeout(Duration.ofSeconds(1))
                .GET()
                .build();
        long deadline = System.nanoTime() + Duration.ofSeconds(20).toNanos();
        while (System.nanoTime() < deadline) {
            if (!process.isAlive()) {
                throw new IllegalStateException("Packaged API process exited before becoming healthy.");
            }
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                JsonNode body = objectMapper.readTree(response.body());
                if (body.path("components").path("qna").path("details").has("catalogueRecords")) {
                    return response;
                }
            } catch (IOException ignored) {
                Thread.sleep(200);
            }
        }
        throw new IllegalStateException("Packaged API did not expose catalogue health before the timeout.");
    }

    private int availablePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    private String executableName() {
        return System.getProperty("os.name").toLowerCase().contains("win") ? "java.exe" : "java";
    }
}
