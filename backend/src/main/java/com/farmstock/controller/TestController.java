package com.farmstock.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("API is working");
    }

    @GetMapping("/debug")
    public ResponseEntity<Map<String, Object>> debug() {
        Map<String, Object> debugInfo = new HashMap<>();
        
        // Database URL info
        String dbUrl = System.getenv("DATABASE_URL");
        debugInfo.put("DATABASE_URL_exists", dbUrl != null);
        debugInfo.put("DATABASE_URL_length", dbUrl != null ? dbUrl.length() : 0);
        
        // Other environment variables
        debugInfo.put("PORT", System.getenv("PORT"));
        debugInfo.put("SPRING_PROFILES_ACTIVE", System.getenv("SPRING_PROFILES_ACTIVE"));
        
        return ResponseEntity.ok(debugInfo);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(health);
    }
}