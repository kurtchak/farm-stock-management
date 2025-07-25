package com.farmstock.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("API is working");
    }

    @GetMapping("/debug")
    public String debug() {
        String dbUrl = System.getenv("DATABASE_URL");
        return "DATABASE_URL exists: " + (dbUrl != null) +
                ", Length: " + (dbUrl != null ? dbUrl.length() : "null");
    }
}