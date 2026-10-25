package io.github.teggr.qna.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.teggr.qna.api.ApiProblemDetails;
import io.github.teggr.qna.security.ClientRateLimiter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ClientRateLimitFilter extends OncePerRequestFilter {
    private final ClientRateLimiter rateLimiter;
    private final ObjectMapper objectMapper;

    public ClientRateLimitFilter(ClientRateLimiter rateLimiter, ObjectMapper objectMapper) {
        this.rateLimiter = rateLimiter;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !("POST".equals(request.getMethod()) && "/api/v1/chat".equals(request.getServletPath()));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!rateLimiter.allow(request.getRemoteAddr())) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            ProblemDetail problem = ApiProblemDetails.create(
                    HttpStatus.TOO_MANY_REQUESTS, "RATE_LIMITED", "Too many requests. Please try again later.");
            ApiProblemDetails.write(objectMapper, response.getOutputStream(), problem);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
