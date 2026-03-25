package com.ezyenglish.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600, allowCredentials = "true")
public class HealthController {

    private final MongoTemplate mongoTemplate;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "EzyEnglish Auth API");
        response.put("port", 8082);
        
        try {
            mongoTemplate.getDb().getName();
            response.put("database", "Connected");
        } catch (Exception e) {
            response.put("database", "Disconnected: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, String>> root() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "EzyEnglish Authentication API");
        response.put("version", "1.0.0");
        response.put("endpoints", "/signup, /signin, /health");
        return ResponseEntity.ok(response);
    }
}
