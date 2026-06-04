package com.quizzler.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Browser origins allowed to call this API. Defaults cover both the
    // docker-compose flow (localhost:4200) and the KIND ingress host
    // (quizzler.localhost); override via CORS_ALLOWED_ORIGINS if either changes.
    @Value("${cors.allowed-origins:http://localhost:4200,http://quizzler.localhost}")
    private String[] allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
