package com.linkty.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // Configure CORS settings for all endpoints.
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")

            // Allow requests from specified origins. (TEMPORARY)
            .allowedOrigins("http://localhost:3000", "http://linkty.kr")

            // Allow all HTTP methods.
            .allowedMethods("*")

            // Allow all headers in the request.
            .allowedHeaders("*")

            // Allow credentials. (ex. cookies, authorization headers)
            .allowCredentials(true);
    }
}
