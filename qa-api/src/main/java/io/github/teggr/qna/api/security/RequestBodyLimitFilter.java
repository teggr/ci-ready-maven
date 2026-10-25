package io.github.teggr.qna.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.teggr.qna.api.ApiProperties;
import io.github.teggr.qna.api.ApiProblemDetails;
import io.github.teggr.qna.security.ClientRateLimiter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RequestBodyLimitFilter extends OncePerRequestFilter {
    private final ApiProperties properties;
    private final ObjectMapper objectMapper;

    public RequestBodyLimitFilter(ApiProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !("POST".equals(request.getMethod()) && "/api/v1/chat".equals(request.getRequestURI()));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        int maxBytes = properties.getMaxRequestBodyBytes();
        if (request.getContentLengthLong() > maxBytes) {
            writeTooLarge(response);
            return;
        }

        byte[] body = request.getInputStream().readNBytes(maxBytes + 1);
        if (body.length > maxBytes) {
            writeTooLarge(response);
            return;
        }
        filterChain.doFilter(new BufferedBodyRequest(request, body), response);
    }

    private void writeTooLarge(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        ProblemDetail problem =
                ApiProblemDetails.create(HttpStatus.PAYLOAD_TOO_LARGE, "REQUEST_TOO_LARGE", "The request body is too large.");
        ApiProblemDetails.write(objectMapper, response.getOutputStream(), problem);
    }

    private static final class BufferedBodyRequest extends HttpServletRequestWrapper {
        private final byte[] body;

        private BufferedBodyRequest(HttpServletRequest request, byte[] body) {
            super(request);
            this.body = body;
        }

        @Override
        public int getContentLength() {
            return body.length;
        }

        @Override
        public long getContentLengthLong() {
            return body.length;
        }

        @Override
        public ServletInputStream getInputStream() {
            ByteArrayInputStream input = new ByteArrayInputStream(body);
            return new ServletInputStream() {
                @Override
                public int read() {
                    return input.read();
                }

                @Override
                public boolean isFinished() {
                    return input.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener readListener) {
                    try {
                        if (isFinished()) {
                            readListener.onAllDataRead();
                        } else {
                            readListener.onDataAvailable();
                        }
                    } catch (IOException exception) {
                        readListener.onError(exception);
                    }
                }
            };
        }

        @Override
        public BufferedReader getReader() throws IOException {
            String encoding = getCharacterEncoding() == null ? StandardCharsets.UTF_8.name() : getCharacterEncoding();
            return new BufferedReader(new InputStreamReader(getInputStream(), encoding));
        }
    }
}
