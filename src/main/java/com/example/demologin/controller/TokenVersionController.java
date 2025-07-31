package com.example.demologin.controller;

import com.example.demologin.annotation.SecuredEndpoint;
import com.example.demologin.annotation.UserAction;
import com.example.demologin.dto.request.BaseActionRequest;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.enums.UserActionType;
import com.example.demologin.service.TokenVersionService;
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
@RequestMapping("/api/token-version")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Token Version Management", description = "APIs for managing user token versions and invalidating tokens")
public class TokenVersionController {
    
    private final TokenVersionService tokenVersionService;
    
    @UserAction(actionType = UserActionType.DELETE, targetType = "TOKEN", 
               description = "Invalidate all own tokens", requiresReason = true)
    @PostMapping("/invalidate-all")
    @SecuredEndpoint("TOKEN_INVALIDATE_OWN")
    @Operation(summary = "Invalidate all tokens for current user", 
               description = "Increment token version to invalidate all existing tokens for the current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "All tokens invalidated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<ResponseObject> invalidateAllCurrentUserTokens(
            @Valid @RequestBody BaseActionRequest request) {
        
        return tokenVersionService.incrementCurrentUserTokenVersion();
    }
    
    @UserAction(actionType = UserActionType.DELETE, targetType = "TOKEN", 
               description = "Admin invalidate user tokens", requiresReason = true)
    @PostMapping("/invalidate-user/{userId}")
    @SecuredEndpoint("TOKEN_INVALIDATE_USER")
    @Operation(summary = "Admin invalidate all tokens for user by ID", 
               description = "Admin operation to increment token version and invalidate all tokens for specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User tokens invalidated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ResponseObject> invalidateUserTokensByUserId(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Valid @RequestBody BaseActionRequest request) {
        
        return tokenVersionService.incrementUserTokenVersionByUserId(userId);
    }
    
    @UserAction(actionType = UserActionType.DELETE, targetType = "TOKEN", 
               description = "Admin invalidate user tokens by username", requiresReason = true)
    @PostMapping("/invalidate-user/username/{username}")
    @SecuredEndpoint("TOKEN_INVALIDATE_USER")
    @Operation(summary = "[DEPRECATED] Admin invalidate all tokens for user by username", 
               description = "⚠️ DEPRECATED: Use /invalidate-user/{userId} instead. Admin operation to increment token version and invalidate all tokens for specific user by username")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User tokens invalidated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @Deprecated
    public ResponseEntity<ResponseObject> invalidateUserTokensByUsername(
            @Parameter(description = "Username") @PathVariable String username,
            @Valid @RequestBody BaseActionRequest request) {
        
        return tokenVersionService.incrementUserTokenVersionByUsername(username);
    }
    
    @GetMapping("/current")
    @SecuredEndpoint("TOKEN_VIEW_OWN")
    @Operation(summary = "Get current user token version", 
               description = "Get the current token version for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token version retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<ResponseObject> getCurrentUserTokenVersion() {
        return tokenVersionService.getCurrentUserTokenVersion();
    }
    
    @GetMapping("/user/{userId}")
    @SecuredEndpoint("TOKEN_VIEW_USER")
    @Operation(summary = "Get user token version by ID", 
               description = "Admin operation to get token version for specific user by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User token version retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ResponseObject> getUserTokenVersionByUserId(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        
        return tokenVersionService.getUserTokenVersionByUserId(userId);
    }
    
    @GetMapping("/user/username/{username}")
    @SecuredEndpoint("TOKEN_VIEW_USER")
    @Operation(summary = "[DEPRECATED] Get user token version by username", 
               description = "⚠️ DEPRECATED: Use /user/{userId} instead. Admin operation to get token version for specific user by username")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User token version retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @Deprecated
    public ResponseEntity<ResponseObject> getUserTokenVersionByUsername(
            @Parameter(description = "Username") @PathVariable String username) {
        
        return tokenVersionService.getUserTokenVersionByUsername(username);
    }
}
