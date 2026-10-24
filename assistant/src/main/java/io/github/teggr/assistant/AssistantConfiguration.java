package io.github.teggr.assistant;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.openai.core.Timeout;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.ai.openai.http.okhttp.OpenAiHttpClientBuilderCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import java.util.List;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(AssistantProperties.class)
public class AssistantConfiguration {
    @Bean
    OpenAiHttpClientBuilderCustomizer assistantHttpTimeouts(AssistantProperties properties) {
        // Spring AI 2 uses the OpenAI SDK's OkHttp transport, not Spring's RestClient.
        return builder -> builder.timeout(Timeout.builder()
                .connect(Duration.ofMillis(properties.connectTimeoutMillis()))
                .read(Duration.ofMillis(properties.readTimeoutMillis()))
                .write(Duration.ofMillis(properties.readTimeoutMillis()))
                .request(Duration.ofMillis(properties.readTimeoutMillis())).build())
                .interceptor(chain -> chain
                        .withConnectTimeout(properties.connectTimeoutMillis(), TimeUnit.MILLISECONDS)
                        .withReadTimeout(properties.readTimeoutMillis(), TimeUnit.MILLISECONDS)
                        .withWriteTimeout(properties.readTimeoutMillis(), TimeUnit.MILLISECONDS)
                        .proceed(chain.request()));
    }

    @Bean
    FilterRegistrationBean<CorsFilter> assistantCors(Environment environment) {
        var cors = new CorsConfiguration();
        cors.setAllowedOrigins(environment.acceptsProfiles(Profiles.of("dev"))
                ? List.of("https://teggr.github.io", "http://localhost:8080")
                : List.of("https://teggr.github.io"));
        cors.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
        cors.setAllowedHeaders(List.of("Content-Type", "HX-Request", "HX-Target",
                "HX-Trigger", "HX-Trigger-Name"));
        cors.setAllowCredentials(false);
        cors.setMaxAge(600L);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/assistant/**", cors);
        var registration = new FilterRegistrationBean<>(new CorsFilter(source));
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
}
