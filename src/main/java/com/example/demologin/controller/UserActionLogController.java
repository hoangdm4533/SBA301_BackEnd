package com.example.demologin.controller;

import com.example.demologin.annotation.SecuredEndpoint;
import com.example.demologin.annotation.UserAction;
import com.example.demologin.dto.request.userActionLog.*;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.enums.UserActionType;
import com.example.demologin.service.UserActionLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ResponseObject> getAllActionLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        return userActionLogService.getAllActionLogs(page, size);
    }

    @GetMapping("/{id}")
    @SecuredEndpoint("LOG_VIEW_ALL")
    @Operation(summary = "Get action log by ID", description = "Retrieve a specific action log by its ID")
    public ResponseEntity<ResponseObject> getActionLogById(
            @Parameter(description = "Action log ID") @PathVariable Long id) {
        
        GetActionLogByIdRequest request = GetActionLogByIdRequest.builder()
                .id(id)
                .build();
                
        return userActionLogService.getActionLogById(request);
    }

    @GetMapping("/user/{userId}")
    @SecuredEndpoint("LOG_VIEW_USER")
    @Operation(summary = "Get action logs by user ID", description = "Retrieve paginated action logs for a specific user")
    public ResponseEntity<ResponseObject> getActionLogsByUserId(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        GetActionLogsByUserIdRequest request = GetActionLogsByUserIdRequest.builder()
                .userId(userId)
                .page(page)
                .size(size)
                .build();
                
        return userActionLogService.getActionLogsByUserId(request);
    }

    @GetMapping("/type/{actionType}")
    @SecuredEndpoint("LOG_SEARCH")
    @Operation(summary = "Get action logs by action type", description = "Retrieve paginated action logs filtered by action type")
    public ResponseEntity<ResponseObject> getActionLogsByActionType(
            @Parameter(description = "Action type") @PathVariable String actionType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        GetActionLogsByTypeRequest request = GetActionLogsByTypeRequest.builder()
                .actionType(actionType)
                .page(page)
                .size(size)
                .build();
                
        return userActionLogService.getActionLogsByActionType(request);
    }

    @GetMapping("/target/{targetType}")
    @SecuredEndpoint("LOG_SEARCH")
    @Operation(summary = "Get action logs by target type", description = "Retrieve paginated action logs filtered by target type")
    public ResponseEntity<ResponseObject> getActionLogsByTargetType(
            @Parameter(description = "Target type") @PathVariable String targetType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        GetActionLogsByTargetTypeRequest request = GetActionLogsByTargetTypeRequest.builder()
                .targetType(targetType)
                .page(page)
                .size(size)
                .build();
                
        return userActionLogService.getActionLogsByTargetType(request);
    }

    @PostMapping("/date-range")
    @SecuredEndpoint("LOG_SEARCH")
    @Operation(summary = "Get action logs by date range", description = "Retrieve paginated action logs within a date range")
    public ResponseEntity<ResponseObject> getActionLogsByDateRange(
            @Valid @RequestBody GetActionLogsByDateRangeRequest request) {
        
        return userActionLogService.getActionLogsByDateRange(request);
    }

    @DeleteMapping("/{id}")
    @SecuredEndpoint("LOG_DELETE")
    @UserAction(actionType = UserActionType.DELETE, targetType = "UserActionLog", description = "Delete user action log")
    @Operation(summary = "Delete action log", description = "Delete a specific action log by its ID")
    public ResponseEntity<ResponseObject> deleteActionLog(
            @Parameter(description = "Action log ID") @PathVariable Long id) {
        
        DeleteActionLogRequest request = DeleteActionLogRequest.builder()
                .id(id)
                .build();
                
        return userActionLogService.deleteActionLog(request);
    }
}
