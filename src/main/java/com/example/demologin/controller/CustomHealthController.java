package com.example.demologin.controller;

import com.example.demologin.annotation.PublicEndpoint;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthContributorRegistry;
import org.springframework.boot.actuate.health.HealthIndicator;
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
    private final HealthContributorRegistry registry;

    public CustomHealthController(HealthEndpoint healthEndpoint, HealthContributorRegistry registry) {
        this.healthEndpoint = healthEndpoint;
        this.registry = registry;
    }

    @PublicEndpoint
    @GetMapping("/v1/health")
    public ResponseEntity<HealthResponse> health() {
        // Lấy health tổng quát
        HealthComponent healthComponent = healthEndpoint.health();
        Status status = healthComponent.getStatus();

        // Lấy health indicator db (nếu có)
        HealthIndicator dbIndicator = (HealthIndicator) registry.getContributor("db");
        Map<String, Object> dbDetails = Collections.emptyMap();
        if (dbIndicator != null) {
            Health dbHealth = dbIndicator.health();
            dbDetails = dbHealth.getDetails();
        }

        // Tạo map details có thể kết hợp toàn bộ hoặc riêng db
        Map<String, Object> details = dbDetails;

        System.out.println("HealthComponent class: " + healthComponent.getClass().getName());
        System.out.println("Health status: " + status.getCode());
        System.out.println("DB health details: " + dbDetails);

        HealthResponse response = new HealthResponse(
                status.getCode(),
                status.equals(Status.UP) ? "Service is healthy" : "Service is unavailable",
                details
        );

        return ResponseEntity.status(status.equals(Status.UP) ? 200 : 503).body(response);
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
