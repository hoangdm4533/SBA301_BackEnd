package com.example.demologin.service;

import com.example.demologin.dto.request.userActivityLog.UserActivityLogExportRequest;
import com.example.demologin.dto.request.userActivityLog.UserActivityLogFilterRequest;
import com.example.demologin.dto.response.PageResponse;
import com.example.demologin.dto.response.UserActivityLogResponse;
import com.example.demologin.entity.User;
import com.example.demologin.entity.UserActivityLog;
import com.example.demologin.enums.ActivityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface UserActivityLogService {
    
    // Manual activity logging
    void logUserActivity(User user, ActivityType activityType, String details);
    void logUserActivity(ActivityType activityType, String details);
    
    // Controller endpoints - return pure data (will be wrapped by @ApiResponse aspect)
    PageResponse<UserActivityLogResponse> getAllActivityLogs(int page, int size);
    UserActivityLogResponse getActivityLogById(Long id);
    PageResponse<UserActivityLogResponse> getActivityLogsByUserId(Long userId, int page, int size);
    PageResponse<UserActivityLogResponse> getActivityLogsByType(String activityType, int page, int size);
    PageResponse<UserActivityLogResponse> getActivityLogsByDateRange(LocalDateTime startTime, LocalDateTime endTime, int page, int size);
    PageResponse<UserActivityLogResponse> exportActivityLogs(UserActivityLogExportRequest request, int page, int size);
    String deleteActivityLog(Long id); // Return success message
    PageResponse<UserActivityLogResponse> getMyLoginHistory(int page, int size);
    
    // Business logic methods (used internally)
    PageResponse<UserActivityLogResponse> getAllActivityLogsInternal(int page, int size);
    UserActivityLogResponse getActivityLogByIdInternal(Long id);
    PageResponse<UserActivityLogResponse> getActivityLogsByUserIdInternal(Long userId, int page, int size);
    PageResponse<UserActivityLogResponse> getActivityLogsByTypeInternal(String activityType, int page, int size);
    PageResponse<UserActivityLogResponse> getActivityLogsByDateRangeInternal(LocalDateTime startTime, LocalDateTime endTime, int page, int size);
    PageResponse<UserActivityLogResponse> searchActivityLogs(UserActivityLogFilterRequest request);
    void deleteActivityLogInternal(Long id);
    PageResponse<UserActivityLogResponse> exportActivityLogsInternal(UserActivityLogExportRequest request, int page, int size);
    
    // Essential data access methods
    Page<UserActivityLog> findAll(Pageable pageable);
    UserActivityLog findById(Long id);
    void deleteById(Long id);
    
    // Utility methods
    void cleanupOldLogs(LocalDateTime cutoffDate);
}
