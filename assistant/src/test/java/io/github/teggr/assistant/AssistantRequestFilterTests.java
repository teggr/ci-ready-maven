package io.github.teggr.assistant;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import static org.assertj.core.api.Assertions.assertThat;

class AssistantRequestFilterTests {
    @Test
    void enforcesByteLimitForChunkedBodyWithUnknownLength() throws Exception {
        var filter = new AssistantRequestFilter(AssistantServiceTests.properties(Map.of()));
        var request = new MockHttpServletRequest("POST", "/assistant/chat") {
            @Override public long getContentLengthLong() { return -1; }
            @Override public int getContentLength() { return -1; }
        };
        request.setContentType("application/x-www-form-urlencoded");
        request.setContent(("question=" + "x".repeat(9000)).getBytes(java.nio.charset.StandardCharsets.UTF_8));
        var response = new MockHttpServletResponse();
        filter.doFilter(request, response, (req, res) -> {
            throw new AssertionError("Oversized chunked body reached controller");
        });
        assertThat(response.getStatus()).isEqualTo(413);
    }

    @Test
    void fixedWindowMapIsBoundedAndExpires() {
        var filter = new AssistantRequestFilter(AssistantServiceTests.properties(Map.of(
                "requests-per-window", 1, "max-rate-limit-clients", 16, "rate-window-seconds", 10)));
        assertThat(filter.accept("first", 0)).isTrue();
        assertThat(filter.accept("first", 1)).isFalse();
        for (int i = 0; i < 15; i++) assertThat(filter.accept("client" + i, 1)).isTrue();
        assertThat(filter.accept("overflow", 1)).isFalse();
        assertThat(filter.accept("overflow", 10001)).isTrue();
        assertThat(filter.accept("first", 10001)).isTrue();
    }

    @Test
    void spoofedForwardedHeadersDoNotBypassLimit() throws Exception {
        var filter = new AssistantRequestFilter(AssistantServiceTests.properties(Map.of("requests-per-window", 1)));
        for (int i = 0; i < 2; i++) {
            var request = new MockHttpServletRequest("POST", "/assistant/chat");
            request.setContentType("application/x-www-form-urlencoded");
            request.setRemoteAddr("192.0.2.10");
            request.addHeader("X-Forwarded-For", "198.51.100." + i);
            request.setContent("question=hello".getBytes(java.nio.charset.StandardCharsets.UTF_8));
            var response = new MockHttpServletResponse();
            filter.doFilter(request, response, (req, res) -> {});
            assertThat(response.getStatus()).isEqualTo(i == 0 ? 200 : 429);
        }
    }
}
