package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.annotation.SecuredEndpoint;
import com.example.demologin.annotation.UserAction;
import com.example.demologin.enums.UserActionType;
import com.example.demologin.service.UserActionLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-action-logs")
@RequiredArgsConstructor
@Tag(name = "User Action Log Management", description = "APIs for managing user action logs (create, update, delete operations)")
public class UserActionLogController {

    private final UserActionLogService userActionLogService;

    @GetMapping
    @SecuredEndpoint("LOG_VIEW_ALL")
    @Operation(summary = "Get all user action logs", description = "Retrieve paginated list of all user action logs")
    @ApiResponse(message = "Action logs retrieved successfully")
    public Object getAllActionLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        return userActionLogService.getAllActionLogs(page, size);
    }

    @GetMapping("/{id}")
    @SecuredEndpoint("LOG_VIEW_ALL")
    @Operation(summary = "Get action log by ID", description = "Retrieve a specific action log by its ID")
    @ApiResponse(message = "Action log retrieved successfully")
    public Object getActionLogById(
            @Parameter(description = "Action log ID") @PathVariable Long id) {
        
        return userActionLogService.getActionLogById(id);
    }

    @GetMapping("/user/{userId}")
    @SecuredEndpoint("LOG_VIEW_USER")
    @Operation(summary = "Get action logs by user ID", description = "Retrieve paginated action logs for a specific user")
    @ApiResponse(message = "User action logs retrieved successfully")
    public Object getActionLogsByUserId(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        return userActionLogService.getActionLogsByUserId(userId, page, size);
    }

    @GetMapping("/type/{actionType}")
    @SecuredEndpoint("LOG_SEARCH")
    @Operation(summary = "Get action logs by action type", description = "Retrieve paginated action logs filtered by action type")
    @ApiResponse(message = "Action logs by type retrieved successfully")
    public Object getActionLogsByActionType(
            @Parameter(description = "Action type") @PathVariable String actionType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        return userActionLogService.getActionLogsByActionType(actionType, page, size);
    }

    @GetMapping("/target/{targetType}")
    @SecuredEndpoint("LOG_SEARCH")
    @Operation(summary = "Get action logs by target type", description = "Retrieve paginated action logs filtered by target type")
    @ApiResponse(message = "Action logs by target type retrieved successfully")
    public Object getActionLogsByTargetType(
            @Parameter(description = "Target type") @PathVariable String targetType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        return userActionLogService.getActionLogsByTargetType(targetType, page, size);
    }

    @GetMapping("/date-range")
    @SecuredEndpoint("LOG_SEARCH")
    @Operation(summary = "Get action logs by date range", description = "Retrieve paginated action logs within a date range")
    @ApiResponse(message = "Action logs by date range retrieved successfully")
    public Object getActionLogsByDateRange(
            @Parameter(description = "Start date (YYYY-MM-DD)") @RequestParam String startDate,
            @Parameter(description = "End date (YYYY-MM-DD)") @RequestParam String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        return userActionLogService.getActionLogsByDateRange(startDate, endDate, page, size);
    }

    @DeleteMapping("/{id}")
    @SecuredEndpoint("LOG_DELETE")
    @UserAction(actionType = UserActionType.DELETE, targetType = "UserActionLog", description = "Delete user action log")
    @Operation(summary = "Delete action log", description = "Delete a specific action log by its ID")
    @ApiResponse(message = "Action log deleted successfully")
    public Object deleteActionLog(
            @Parameter(description = "Action log ID") @PathVariable Long id) {
        
        return userActionLogService.deleteActionLog(id);
    }
}
