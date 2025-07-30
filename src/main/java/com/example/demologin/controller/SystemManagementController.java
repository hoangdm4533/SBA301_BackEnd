package com.example.demologin.controller;

import com.example.demologin.annotation.SecuredEndpoint;
import com.example.demologin.dto.response.ResponseObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Example controller demonstrating class-level @SecuredEndpoint usage.
 * All methods in this controller require ADMIN_ACCESS permission.
 */
@RestController
@RequestMapping("/api/admin/system")
@RequiredArgsConstructor
@Tag(name = "System Management", description = "Admin-only system management endpoints")
@SecuredEndpoint("ADMIN_ACCESS")  // Class-level: applies to all methods
public class SystemManagementController {

    @GetMapping("/status")
    @Operation(summary = "Get system status", description = "Get current system status and health")
    public ResponseEntity<ResponseObject> getSystemStatus() {
        // This method inherits ADMIN_ACCESS requirement from class-level annotation
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "System is running", "OK"));
    }

    @GetMapping("/metrics")
    @Operation(summary = "Get system metrics", description = "Get system performance metrics")
    public ResponseEntity<ResponseObject> getSystemMetrics() {
        // This method also inherits ADMIN_ACCESS requirement from class-level annotation
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "System metrics retrieved", "metrics_data"));
    }

    @PostMapping("/restart")
    @Operation(summary = "Restart system", description = "Restart system components")
    public ResponseEntity<ResponseObject> restartSystem() {
        // This method also inherits ADMIN_ACCESS requirement from class-level annotation
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "System restart initiated", null));
    }

    // Example of method-level override (if needed)
    @GetMapping("/public-info")
    @Operation(summary = "Get public system info", description = "Get non-sensitive system information")
    @SecuredEndpoint("USER_ACCESS")  // Method-level: overrides class-level ADMIN_ACCESS
    public ResponseEntity<ResponseObject> getPublicSystemInfo() {
        // This method requires only USER_ACCESS, overriding the class-level ADMIN_ACCESS
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Public system info", "version: 1.0"));
    }
}
