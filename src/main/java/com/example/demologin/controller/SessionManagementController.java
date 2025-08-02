package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.annotation.SecuredEndpoint;
import com.example.demologin.annotation.UserAction;
import com.example.demologin.annotation.UserActivity;
import com.example.demologin.dto.request.BaseActionRequest;
import com.example.demologin.enums.ActivityType;
import com.example.demologin.enums.UserActionType;
import com.example.demologin.service.SessionManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/session")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Session Management", description = "APIs for managing user sessions and device logout")
    public class SessionManagementController {
    
    private final SessionManagementService sessionManagementService;
    
    @UserActivity(activityType = ActivityType.LOGOUT, details = "User logout from current device")
    @PostMapping("/logout")
    @SecuredEndpoint("USER_TOKEN_MANAGEMENT")
    @Operation(summary = "Logout from current device", 
               description = "Logout user from current device only")
    @ApiResponse(message = "Logged out from current device successfully")
    public Object logoutCurrentDevice() {
        return sessionManagementService.logoutCurrentDevice();
    }
    
    @UserActivity(activityType = ActivityType.LOGOUT, details = "User logout from all devices")
    @PostMapping("/logout-all")
    @SecuredEndpoint("USER_TOKEN_MANAGEMENT")
    @Operation(summary = "Logout from all devices", 
               description = "Logout user from all devices by invalidating all tokens")
    @ApiResponse(message = "Logged out from all devices successfully")
    public Object logoutFromAllDevices(
            @Valid @RequestBody BaseActionRequest request) {
        
        return sessionManagementService.logoutFromAllDevices(request);
    }
    
    @UserAction(actionType = UserActionType.UPDATE, targetType = "USER", 
               description = "Admin force logout user from all devices", requiresReason = true)
    @PostMapping("/force-logout/{userId}")
    @SecuredEndpoint("ADMIN_USER_MANAGEMENT")
    @Operation(summary = "Admin force logout user from all devices", 
               description = "Admin operation to force logout a specific user from all devices")
    @ApiResponse(message = "User forced logout successfully")
    public Object forceLogoutUser(
            @Parameter(description = "User ID to force logout") @PathVariable Long userId,
            @Valid @RequestBody BaseActionRequest request) {
        
        return sessionManagementService.forceLogoutUser(userId, request);
    }
    
    @GetMapping("/active-count")
    @SecuredEndpoint("USER_TOKEN_MANAGEMENT")
    @Operation(summary = "Get estimated active session count", 
               description = "Get estimated number of active sessions for current user")
    @ApiResponse(message = "Active session count retrieved successfully")
    public Object getActiveSessionCount() {
        return sessionManagementService.getActiveSessionCount();
    }
    
    @GetMapping("/active-count/{userId}")
    @SecuredEndpoint("ADMIN_USER_MANAGEMENT")
    @Operation(summary = "Get estimated active session count for user", 
               description = "Admin operation to get estimated active session count for specific user")
    @ApiResponse(message = "User active session count retrieved successfully")
    public Object getUserActiveSessionCount(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        
        return sessionManagementService.getUserSessionStatus(userId);
    }
}
