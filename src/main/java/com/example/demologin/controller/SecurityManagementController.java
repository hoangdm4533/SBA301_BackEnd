package com.example.demologin.controller;

import com.example.demologin.annotation.SecuredEndpoint;
import com.example.demologin.annotation.UserActivity;
import com.example.demologin.dto.request.BaseActionRequest;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.enums.ActivityType;
import com.example.demologin.enums.UserStatus;
import com.example.demologin.service.SecurityManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/security")
@RequiredArgsConstructor
@Tag(name = "Security Management", description = "APIs for managing account security, lockouts, and login attempts")
public class SecurityManagementController {

    private final SecurityManagementService securityManagementService;

    @PostMapping("/unlock-account/{username}")
    @SecuredEndpoint("ADMIN_SECURITY_MANAGEMENT")
    @UserActivity(activityType = ActivityType.ADMIN_ACTION, details = "Admin unlock user account", logEditorId = true)
    @Operation(summary = "Unlock user account", description = "Admin operation to unlock a locked user account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account unlocked successfully"),
        @ApiResponse(responseCode = "404", description = "Account not found or not locked"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<ResponseObject> unlockAccount(
            @Parameter(description = "Username to unlock") @PathVariable String username) {
        
        return securityManagementService.unlockAccount(username);
    }

    @PostMapping("/lock-account/{username}")
    @SecuredEndpoint("ADMIN_SECURITY_MANAGEMENT")
    @UserActivity(activityType = ActivityType.ADMIN_ACTION, details = "Admin lock user account", logEditorId = true)
    @Operation(summary = "Lock user account", description = "Admin operation to manually lock a user account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account locked successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<ResponseObject> lockAccount(
            @Parameter(description = "Username to lock") @PathVariable String username,
            @Valid @RequestBody BaseActionRequest request) {
        
        return securityManagementService.lockAccount(username, request);
    }

    @PutMapping("/change-status/{username}")
    @SecuredEndpoint("ADMIN_USER_MANAGEMENT")
    @UserActivity(activityType = ActivityType.ADMIN_ACTION, details = "Admin change user status", logEditorId = true)
    @Operation(summary = "Change user status", description = "Admin operation to change user account status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User status changed successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<ResponseObject> changeUserStatus(
            @Parameter(description = "Username") @PathVariable String username,
            @Parameter(description = "New status") @RequestParam UserStatus status,
            @Valid @RequestBody BaseActionRequest request) {
        
        return securityManagementService.changeUserStatus(username, status, request);
    }

    @GetMapping("/lockouts")
    @SecuredEndpoint("ADMIN_SECURITY_MANAGEMENT")
    @Operation(summary = "Get account lockouts", description = "Get paginated list of account lockouts")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account lockouts retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<ResponseObject> getAccountLockouts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "true") boolean activeOnly) {
        
        return securityManagementService.getAccountLockouts(page, size, activeOnly);
    }

    @GetMapping("/login-attempts/{username}")
    @SecuredEndpoint("ADMIN_SECURITY_MANAGEMENT")
    @Operation(summary = "Get login attempts for user", description = "Get login attempts for specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login attempts retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<ResponseObject> getLoginAttempts(
            @Parameter(description = "Username") @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "24") int hoursBack) {
        
        return securityManagementService.getLoginAttempts(username, page, size, hoursBack);
    }

    @GetMapping("/lockout-status/{username}")
    @SecuredEndpoint("ADMIN_SECURITY_MANAGEMENT")
    @Operation(summary = "Check account lockout status", description = "Check if account is locked and get details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lockout status retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<ResponseObject> getLockoutStatus(
            @Parameter(description = "Username") @PathVariable String username) {
        
        return securityManagementService.getLockoutStatus(username);
    }
}
