package com.example.demologin.controller;

import com.example.demologin.annotation.PublicEndpoint;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
public class CustomHealthController {

    private final HealthEndpoint healthEndpoint;

    public CustomHealthController(HealthEndpoint healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }

    @PublicEndpoint
    @GetMapping("/v1/health")
    public ResponseEntity<HealthResponse> health() {
        HealthComponent healthComponent = healthEndpoint.health();
        Status status = healthComponent.getStatus();

        Map<String, Object> details = healthComponent instanceof Health ?
                ((Health) healthComponent).getDetails() :
                Collections.emptyMap();

        HealthResponse response = new HealthResponse(
                status.getCode(),
                status.equals(Status.UP) ? "Service is healthy" : "Service is unavailable",
                details
        );

        return ResponseEntity
                .status(status.equals(Status.UP) ? 200 : 503)
                .body(response);
    }

    public static class HealthResponse {
        private final String status;
        private final String message;
        private final Map<String, Object> details;

        public HealthResponse(String status, String message, Map<String, Object> details) {
            this.status = status;
            this.message = message;
            this.details = details;
        }

        // Getters
        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public Map<String, Object> getDetails() {
            return details;
        }
    }
}