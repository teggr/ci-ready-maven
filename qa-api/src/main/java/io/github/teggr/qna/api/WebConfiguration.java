package io.github.teggr.qna.api;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    private final ApiProperties properties;

    public WebConfiguration(ApiProperties properties) {
        this.properties = properties;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/v1/chat")
                .allowedOrigins(properties.getCorsAllowedOrigin())
                .allowedMethods("POST", "OPTIONS")
                .allowedHeaders("Content-Type")
                .maxAge(3600);
    }
}
