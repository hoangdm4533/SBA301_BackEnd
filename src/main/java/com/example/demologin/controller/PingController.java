package com.example.demologin.controller;

import com.example.demologin.annotation.PublicEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
public class PingController {

    @PublicEndpoint
    @GetMapping("/api/ping")
    public Map<String, Object> ping() {
        return Map.of(
                "status", "UP",
                "timestamp", Instant.now().toString()
        );
    }
}
