package com.example.demologin.controller;

import com.example.demologin.annotation.SecuredEndpoint;
import com.example.demologin.annotation.UserAction;
import com.example.demologin.dto.request.UserActivityLogExportRequest;
import com.example.demologin.dto.response.PageResponse;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.dto.response.UserActivityLogResponse;
import com.example.demologin.enums.UserActionType;
import com.example.demologin.service.UserActivityLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/user-activity-logs")
@RequiredArgsConstructor
@Tag(name = "User Activity Log Management", description = "APIs for managing user activity logs (login, logout, registration, etc.)")
public class UserActivityLogController {

    private final UserActivityLogService userActivityLogService;

    @GetMapping
    @SecuredEndpoint("LOG_VIEW_ACTIVITY")
    @Operation(summary = "Get all user activity logs", description = "Retrieve paginated list of all user activity logs")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Activity logs retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<ResponseObject> getAllActivityLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        PageResponse<UserActivityLogResponse> response = userActivityLogService.getAllActivityLogs(page, size);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "User activity logs retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @SecuredEndpoint("LOG_VIEW_ACTIVITY")
    @Operation(summary = "Get activity log by ID", description = "Retrieve a specific activity log by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Activity log retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Activity log not found")
    })
    public ResponseEntity<ResponseObject> getActivityLogById(
            @Parameter(description = "Activity log ID") @PathVariable Long id) {
        
        UserActivityLogResponse response = userActivityLogService.getActivityLogById(id);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Activity log retrieved successfully", response));
    }

    @GetMapping("/user/{userId}")
    @SecuredEndpoint("LOG_VIEW_ACTIVITY")
    @Operation(summary = "Get activity logs by user ID", description = "Retrieve paginated activity logs for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Activity logs retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ResponseObject> getActivityLogsByUserId(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        PageResponse<UserActivityLogResponse> response = userActivityLogService.getActivityLogsByUserId(userId, page, size);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Activity logs retrieved successfully", response));
    }

    @GetMapping("/type/{actionType}")
    @SecuredEndpoint("LOG_VIEW_ACTIVITY")
    @Operation(summary = "Get activity logs by action type", description = "Retrieve paginated activity logs filtered by action type")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Activity logs retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "400", description = "Invalid action type")
    })
    public ResponseEntity<ResponseObject> getActivityLogsByActionType(
            @Parameter(description = "Action type") @PathVariable String actionType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        PageResponse<UserActivityLogResponse> response = userActivityLogService.getActivityLogsByType(actionType, page, size);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Activity logs retrieved successfully", response));
    }

    @GetMapping("/date-range")
    @SecuredEndpoint("LOG_VIEW_ACTIVITY")
    @Operation(summary = "Get activity logs by date range", description = "Retrieve paginated activity logs within a date range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Activity logs retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "400", description = "Invalid date range")
    })
    public ResponseEntity<ResponseObject> getActivityLogsByDateRange(
            @Parameter(description = "Start date (YYYY-MM-DD)") @RequestParam String startDate,
            @Parameter(description = "End date (YYYY-MM-DD)") @RequestParam String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(endDate).atTime(23, 59, 59);
        PageResponse<UserActivityLogResponse> response = userActivityLogService.getActivityLogsByDateRange(startDateTime, endDateTime, page, size);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Activity logs retrieved successfully", response));
    }

    @UserAction(actionType = UserActionType.CREATE, targetType = "LOG", 
               description = "Export activity logs", requiresReason = true)
    @PostMapping("/export")
    @SecuredEndpoint("ADMIN_ACTIVITY_LOG_EXPORT")
    @Operation(summary = "Export activity logs", description = "Export activity logs within date range with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Activity logs exported successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<ResponseObject> exportActivityLogs(
            @Valid @RequestBody UserActivityLogExportRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        PageResponse<UserActivityLogResponse> response = userActivityLogService.exportActivityLogs(request, page, size);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Activity logs exported successfully", response));
    }

    @UserAction(actionType = UserActionType.DELETE, targetType = "LOG", 
               description = "Delete activity log", requiresReason = true)
    @DeleteMapping("/{id}")
    @SecuredEndpoint("LOG_DELETE")
    @Operation(summary = "Delete activity log", description = "Delete a specific activity log by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Activity log deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Activity log not found")
    })
    public ResponseEntity<ResponseObject> deleteActivityLog(
            @Parameter(description = "Activity log ID") @PathVariable Long id) {
        
        userActivityLogService.deleteActivityLog(id);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Activity log deleted successfully", null));
    }
}
