package io.github.teggr.qna;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class QaApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(QaApiApplication.class, args);
    }
}
