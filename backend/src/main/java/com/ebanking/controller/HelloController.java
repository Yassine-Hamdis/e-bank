package com.ebanking.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Sample REST Controller that demonstrates a simple API endpoint
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:45571", "http://localhost:4200"}, maxAge = 3600)
public class HelloController {

    /**
     * Simple GET endpoint that returns a greeting message
     *
     * @return ResponseEntity containing a greeting message
     */
    @GetMapping("/hello")
    public ResponseEntity<Map<String, Object>> sayHello() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "E-Banking Application is running successfully!");
        response.put("status", "success");
        response.put("features", "User Management & Authentication System Active");
        response.put("timestamp", System.currentTimeMillis());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
