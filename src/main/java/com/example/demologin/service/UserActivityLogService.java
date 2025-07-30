package com.example.demologin.service;

import com.example.demologin.dto.request.UserActivityLogExportRequest;
import com.example.demologin.dto.request.UserActivityLogFilterRequest;
import com.example.demologin.dto.response.PageResponse;
import com.example.demologin.dto.response.UserActivityLogResponse;
import com.example.demologin.entity.UserActivityLog;
import com.example.demologin.enums.ActivityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface UserActivityLogService {
    
    // Business logic methods (used by controllers)
    PageResponse<UserActivityLogResponse> getAllActivityLogs(int page, int size);
    UserActivityLogResponse getActivityLogById(Long id);
    PageResponse<UserActivityLogResponse> getActivityLogsByUserId(Long userId, int page, int size);
    PageResponse<UserActivityLogResponse> getActivityLogsByType(String activityType, int page, int size);
    PageResponse<UserActivityLogResponse> getActivityLogsByDateRange(LocalDateTime startTime, LocalDateTime endTime, int page, int size);
    PageResponse<UserActivityLogResponse> searchActivityLogs(UserActivityLogFilterRequest request);
    void deleteActivityLog(Long id);
    PageResponse<UserActivityLogResponse> exportActivityLogs(UserActivityLogExportRequest request, int page, int size);
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
