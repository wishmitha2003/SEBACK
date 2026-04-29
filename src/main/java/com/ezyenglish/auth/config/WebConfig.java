package com.ezyenglish.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(@org.springframework.lang.NonNull ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get("uploads").toAbsolutePath();
        String uploadLocation = uploadDir.toUri().toString();
        if (!uploadLocation.endsWith("/")) {
            uploadLocation += "/";
        }
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadLocation);
        
        // Also serve pronunciation folder specifically
        Path pronDir = Paths.get("uploads/pronunciation").toAbsolutePath();
        String pronLocation = pronDir.toUri().toString();
        if (!pronLocation.endsWith("/")) {
            pronLocation += "/";
        }
        
        registry.addResourceHandler("/uploads/pronunciation/**")
                .addResourceLocations(pronLocation);
    }

    @Override
    public void addCorsMappings(@org.springframework.lang.NonNull CorsRegistry registry) {
        registry.addMapping("/uploads/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "HEAD", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
