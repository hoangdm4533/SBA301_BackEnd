package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.annotation.SecuredEndpoint;
import com.example.demologin.annotation.UserAction;
import com.example.demologin.dto.request.BaseActionRequest;
import com.example.demologin.enums.UserActionType;
import com.example.demologin.enums.UserStatus;
import com.example.demologin.service.SecurityManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/security")
@RequiredArgsConstructor
@Tag(name = "Security Management", description = "APIs for managing account security, lockouts, and login attempts")
    public class SecurityManagementController {

    private final SecurityManagementService securityManagementService;

    @UserAction(actionType = UserActionType.UPDATE, targetType = "USER", 
               description = "Admin unlock user account", requiresReason = true)
    @PostMapping("/unlock-account/{userId}")
    @SecuredEndpoint("ADMIN_SECURITY_MANAGEMENT")
    @Operation(summary = "Unlock user account", description = "Admin operation to unlock a locked user account")
    @ApiResponse(message = "Account unlocked successfully")
    public Object unlockAccount(
            @Parameter(description = "User ID to unlock") @PathVariable Long userId) {
        
        return securityManagementService.unlockAccountById(userId);
    }

    @UserAction(actionType = UserActionType.UPDATE, targetType = "USER", 
               description = "Admin lock user account", requiresReason = true)
    @PostMapping("/lock-account/{userId}")
    @SecuredEndpoint("ADMIN_SECURITY_MANAGEMENT")
    @Operation(summary = "Lock user account", description = "Admin operation to manually lock a user account")
    @ApiResponse(message = "Account locked successfully")
    public Object lockAccount(
            @Parameter(description = "User ID to lock") @PathVariable Long userId,
            @Valid @RequestBody BaseActionRequest request) {
        
        return securityManagementService.lockAccountById(userId, request);
    }

    @UserAction(actionType = UserActionType.UPDATE, targetType = "USER", 
               description = "Admin change user status", requiresReason = true)
    @PutMapping("/change-status/{userId}")
    @SecuredEndpoint("ADMIN_USER_MANAGEMENT")
    @Operation(summary = "Change user status", description = "Admin operation to change user account status")
    @ApiResponse(message = "User status changed successfully")
    public Object changeUserStatus(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "New status") @RequestParam UserStatus status,
            @Valid @RequestBody BaseActionRequest request) {
        
        return securityManagementService.changeUserStatusById(userId, status, request);
    }

    @GetMapping("/lockouts")
    @SecuredEndpoint("ADMIN_SECURITY_MANAGEMENT")
    @Operation(summary = "Get account lockouts", description = "Get paginated list of account lockouts")
    @ApiResponse(message = "Account lockouts retrieved successfully")
    public Object getAccountLockouts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "true") boolean activeOnly) {
        
        return securityManagementService.getAccountLockouts(page, size, activeOnly);
    }

    @GetMapping("/login-attempts/{userId}")
    @SecuredEndpoint("ADMIN_SECURITY_MANAGEMENT")
    @Operation(summary = "Get login attempts for user", description = "Get login attempts for specific user")
    @ApiResponse(message = "Login attempts retrieved successfully")
    public Object getLoginAttempts(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "24") int hoursBack) {
        
        return securityManagementService.getLoginAttemptsByUserId(userId, page, size, hoursBack);
    }

    @GetMapping("/lockout-status/{userId}")
    @SecuredEndpoint("ADMIN_SECURITY_MANAGEMENT")
    @Operation(summary = "Check account lockout status", description = "Check if account is locked and get details")
    @ApiResponse(message = "Lockout status retrieved successfully")
    public Object getLockoutStatus(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        
        return securityManagementService.getLockoutStatusByUserId(userId);
    }
}
