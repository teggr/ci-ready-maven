package io.github.teggr.assistant;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class AssistantRequestFilter extends OncePerRequestFilter {
    private final AssistantProperties properties;
    private final Map<String, Window> clients = new HashMap<>();

    public AssistantRequestFilter(AssistantProperties properties) {
        this.properties = properties;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().equals(request.getContextPath() + "/assistant/chat");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain chain) throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("X-Content-Type-Options", "nosniff");
        if (!"POST".equals(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }
        if (!accept(request.getRemoteAddr(), System.currentTimeMillis())) {
            reject(response, 429, "Too many requests. Please try again later.");
            return;
        }
        try {
            var contentType = MediaType.parseMediaType(request.getContentType() == null ? "" : request.getContentType());
            if (!"application".equalsIgnoreCase(contentType.getType())
                    || !"x-www-form-urlencoded".equalsIgnoreCase(contentType.getSubtype())) {
                reject(response, 415, "Use a form-encoded request.");
                return;
            }
        } catch (IllegalArgumentException ex) {
            reject(response, 415, "Use a form-encoded request.");
            return;
        }
        if (request.getContentLengthLong() > properties.maxRequestBytes()) {
            reject(response, 413, "Request too large.");
            return;
        }
        byte[] body = request.getInputStream().readNBytes(properties.maxRequestBytes() + 1);
        if (body.length > properties.maxRequestBytes()) {
            reject(response, 413, "Request too large.");
            return;
        }
        chain.doFilter(new HttpServletRequestWrapper(request) {
            @Override
            public ServletInputStream getInputStream() {
                var input = new ByteArrayInputStream(body);
                return new ServletInputStream() {
                    @Override public int read() { return input.read(); }
                    @Override public int read(byte[] b, int off, int len) { return input.read(b, off, len); }
                    @Override public boolean isFinished() { return input.available() == 0; }
                    @Override public boolean isReady() { return true; }
                    @Override public void setReadListener(ReadListener listener) {
                        throw new UnsupportedOperationException("Synchronous input only");
                    }
                };
            }
        }, response);
    }

    synchronized boolean accept(String address, long now) {
        long duration = properties.rateWindowSeconds() * 1000L;
        clients.entrySet().removeIf(entry -> now - entry.getValue().start >= duration);
        Window window = clients.get(address);
        if (window == null) {
            if (clients.size() >= properties.maxRateLimitClients()) return false;
            window = new Window(now);
            clients.put(address, window);
        }
        if (window.count >= properties.requestsPerWindow()) return false;
        window.count++;
        return true;
    }

    private static void reject(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write("<p role=\"status\">" + message + "</p>");
    }

    private static final class Window {
        final long start;
        int count;
        Window(long start) { this.start = start; }
    }
}
