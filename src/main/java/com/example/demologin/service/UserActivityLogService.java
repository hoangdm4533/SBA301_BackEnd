package com.example.demologin.service;

import com.example.demologin.dto.request.userActivityLog.UserActivityLogExportRequest;
import com.example.demologin.dto.request.userActivityLog.UserActivityLogFilterRequest;
import com.example.demologin.dto.response.PageResponse;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.dto.response.UserActivityLogResponse;
import com.example.demologin.entity.User;
import com.example.demologin.entity.UserActivityLog;
import com.example.demologin.enums.ActivityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface UserActivityLogService {
    
    // Manual activity logging
    void logUserActivity(User user, ActivityType activityType, String details);
    void logUserActivity(ActivityType activityType, String details);
    
    // Controller endpoints - return ResponseEntity<ResponseObject>
    ResponseEntity<ResponseObject> getAllActivityLogs(int page, int size);
    ResponseEntity<ResponseObject> getActivityLogById(Long id);
    ResponseEntity<ResponseObject> getActivityLogsByUserId(Long userId, int page, int size);
    ResponseEntity<ResponseObject> getActivityLogsByType(String activityType, int page, int size);
    ResponseEntity<ResponseObject> getActivityLogsByDateRange(LocalDateTime startTime, LocalDateTime endTime, int page, int size);
    ResponseEntity<ResponseObject> exportActivityLogs(UserActivityLogExportRequest request, int page, int size);
    ResponseEntity<ResponseObject> deleteActivityLog(Long id);
    ResponseEntity<ResponseObject> getMyLoginHistory(int page, int size);
    
    // Business logic methods (used internally)
    PageResponse<UserActivityLogResponse> getAllActivityLogsInternal(int page, int size);
    UserActivityLogResponse getActivityLogByIdInternal(Long id);
    PageResponse<UserActivityLogResponse> getActivityLogsByUserIdInternal(Long userId, int page, int size);
    PageResponse<UserActivityLogResponse> getActivityLogsByTypeInternal(String activityType, int page, int size);
    PageResponse<UserActivityLogResponse> getActivityLogsByDateRangeInternal(LocalDateTime startTime, LocalDateTime endTime, int page, int size);
    PageResponse<UserActivityLogResponse> searchActivityLogs(UserActivityLogFilterRequest request);
    void deleteActivityLogInternal(Long id);
    PageResponse<UserActivityLogResponse> exportActivityLogsInternal(UserActivityLogExportRequest request, int page, int size);
    Map<ActivityType, Long> getActivityStats(LocalDateTime startTime);
    
    // Raw data access methods (for internal service use)
    Page<UserActivityLog> findAll(Pageable pageable);
    UserActivityLog findById(Long id);
    void deleteById(Long id);
    Page<UserActivityLog> findByUserId(Long userId, Pageable pageable);
    Page<UserActivityLog> findByActivityType(ActivityType activityType, Pageable pageable);
    Page<UserActivityLog> findByStatus(String status, Pageable pageable);
    Page<UserActivityLog> findByDateRange(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    Page<UserActivityLog> findWithFilters(Long userId, ActivityType activityType, String status,
                                         LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    List<UserActivityLog> findByDateRangeForExport(LocalDateTime startTime, LocalDateTime endTime);
    Long getUserActivityCount(Long userId, LocalDateTime startTime);
    
    // Utility methods
    void cleanupOldLogs(LocalDateTime cutoffDate);
}
