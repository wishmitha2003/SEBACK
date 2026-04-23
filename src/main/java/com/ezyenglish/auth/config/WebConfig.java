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
        String uploadPath = uploadDir.toString().replace("\\", "/");
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath + "/");
        
        // Also serve pronunciation folder specifically
        Path pronDir = Paths.get("uploads/pronunciation").toAbsolutePath();
        String pronPath = pronDir.toString().replace("\\", "/");
        registry.addResourceHandler("/uploads/pronunciation/**")
                .addResourceLocations("file:" + pronPath + "/");
    }

    @Override
    public void addCorsMappings(@org.springframework.lang.NonNull CorsRegistry registry) {
        registry.addMapping("/uploads/**")
                .allowedOrigins("http://localhost:5173", "http://localhost:3000")
                .allowedMethods("GET", "HEAD", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
