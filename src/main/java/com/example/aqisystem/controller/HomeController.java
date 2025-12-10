package com.example.aqisystem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "AQI System API is running!");
        response.put("status", "active");
        response.put("version", "1.0.0");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("GET /api/data", "Get AQI and weather data by city - Parameters: city (required), units (optional, default: metric)");
        endpoints.put("GET /api/users", "Get all users");
        endpoints.put("POST /api/users/register", "Register a new user");
        endpoints.put("GET /api/test/email", "Test email sending - Parameters: to (required)");
        endpoints.put("GET /api/test/trigger-alert", "Manually trigger alert for a user - Parameters: userId (required)");
        endpoints.put("GET /api/test/trigger-all-alerts", "Manually trigger alerts for all users (simulates scheduler)");
        endpoints.put("GET /api/test/email-config", "Check email service configuration");
        
        response.put("availableEndpoints", endpoints);
        
        return ResponseEntity.ok(response);
    }
}

