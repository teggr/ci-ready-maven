package io.github.teggr.qna.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.teggr.qna.api.ApiProperties;
import io.github.teggr.qna.api.security.ClientRateLimitFilter;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class ClientRateLimitFilterTest {
    @Test
    void rejectsRequestsOverTheLimitWithProblemDetails() throws Exception {
        ApiProperties properties = new ApiProperties();
        properties.setRequestsPerWindow(1);
        ClientRateLimiter limiter = new ClientRateLimiter(properties);
        ClientRateLimitFilter filter = new ClientRateLimitFilter(limiter, new ObjectMapper());
        MockHttpServletRequest request = chatRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = mock(MockFilterChain.class);

        assertTrue(limiter.allow(request.getRemoteAddr()));
        filter.doFilter(request, response, chain);

        assertEquals(429, response.getStatus());
        assertTrue(MediaType.APPLICATION_PROBLEM_JSON.isCompatibleWith(MediaType.parseMediaType(response.getContentType())));
        var problem = new ObjectMapper().readTree(response.getContentAsString());
        assertEquals(429, problem.get("status").asInt());
        assertEquals("Too Many Requests", problem.get("title").asText());
        assertEquals("Too many requests. Please try again later.", problem.get("detail").asText());
        assertEquals("RATE_LIMITED", problem.get("code").asText());
        verifyNoInteractions(chain);
    }

    @Test
    void allowsRequestsWithinTheLimitToContinue() throws Exception {
        ClientRateLimitFilter filter =
                new ClientRateLimitFilter(new ClientRateLimiter(new ApiProperties()), new ObjectMapper());
        MockHttpServletRequest request = chatRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = mock(MockFilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    void onlyAppliesToChatPostRequests() throws Exception {
        ClientRateLimitFilter filter =
                new ClientRateLimitFilter(new ClientRateLimiter(new ApiProperties()), new ObjectMapper());
        MockHttpServletRequest request = chatRequest();
        request.setMethod("GET");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertSame(request, chain.getRequest());
        assertNotNull(chain.getResponse());
    }

    private MockHttpServletRequest chatRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setServletPath("/api/v1/chat");
        request.setRemoteAddr("192.0.2.1");
        return request;
    }
}
