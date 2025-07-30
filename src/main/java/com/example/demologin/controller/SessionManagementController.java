package com.example.demologin.controller;

import com.example.demologin.annotation.SecuredEndpoint;
import com.example.demologin.annotation.UserActivity;
import com.example.demologin.dto.request.BaseActionRequest;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.enums.ActivityType;
import com.example.demologin.service.SessionManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/session")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Session Management", description = "APIs for managing user sessions and device logout")
public class SessionManagementController {
    
    private final SessionManagementService sessionManagementService;
    
    @PostMapping("/logout")
    @UserActivity(activityType = ActivityType.LOGOUT, details = "User logout from current device")
    @SecuredEndpoint("USER_TOKEN_MANAGEMENT")
    @Operation(summary = "Logout from current device", 
               description = "Logout user from current device only")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logged out successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ResponseObject> logoutCurrentDevice() {
        ResponseObject response = sessionManagementService.logoutCurrentDevice();
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout-all")
    @UserActivity(activityType = ActivityType.LOGOUT, details = "User logout from all devices")
    @SecuredEndpoint("USER_TOKEN_MANAGEMENT")
    @Operation(summary = "Logout from all devices", 
               description = "Logout user from all devices by invalidating all tokens")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logged out from all devices successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ResponseObject> logoutFromAllDevices(
            @Valid @RequestBody BaseActionRequest request) {
        
        ResponseObject response = sessionManagementService.logoutFromAllDevices(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/force-logout/{userId}")
    @UserActivity(activityType = ActivityType.LOGOUT, details = "Admin force logout user from all devices", logEditorId = true)
    @SecuredEndpoint("ADMIN_USER_MANAGEMENT")
    @Operation(summary = "Admin force logout user from all devices", 
               description = "Admin operation to force logout a specific user from all devices")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User logged out from all devices successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ResponseObject> forceLogoutUser(
            @Parameter(description = "User ID to force logout") @PathVariable Long userId,
            @Valid @RequestBody BaseActionRequest request) {
        
        ResponseObject response = sessionManagementService.forceLogoutUser(userId, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/active-count")
    @SecuredEndpoint("USER_TOKEN_MANAGEMENT")
    @Operation(summary = "Get estimated active session count", 
               description = "Get estimated number of active sessions for current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active session count retrieved"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ResponseObject> getActiveSessionCount() {
        ResponseObject response = sessionManagementService.getActiveSessionCount();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/active-count/{userId}")
    @SecuredEndpoint("ADMIN_USER_MANAGEMENT")
    @Operation(summary = "Get estimated active session count for user", 
               description = "Admin operation to get estimated active session count for specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active session count retrieved"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ResponseObject> getUserActiveSessionCount(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        
        ResponseObject response = sessionManagementService.getUserSessionStatus(userId);
        return ResponseEntity.ok(response);
    }
}
