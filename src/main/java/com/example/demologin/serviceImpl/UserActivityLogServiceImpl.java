package com.example.demologin.serviceImpl;

import com.example.demologin.dto.request.userActivityLog.UserActivityLogExportRequest;
import com.example.demologin.dto.request.userActivityLog.UserActivityLogFilterRequest;
import com.example.demologin.dto.response.PageResponse;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.dto.response.UserActivityLogResponse;
import com.example.demologin.entity.UserActivityLog;
import com.example.demologin.enums.ActivityType;
import com.example.demologin.exception.exceptions.NotFoundException;
import com.example.demologin.exception.exceptions.NotFoundException;
import com.example.demologin.mapper.UserActivityLogMapper;
import com.example.demologin.repository.UserActivityLogRepository;
import com.example.demologin.service.UserActivityLogService;
import com.example.demologin.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserActivityLogServiceImpl implements UserActivityLogService {

    private final UserActivityLogRepository userActivityLogRepository;
    private final UserActivityLogMapper userActivityLogMapper;

    // Controller endpoint implementations
    @Override
    public ResponseEntity<ResponseObject> getAllActivityLogs(int page, int size) {
        try {
            PageResponse<UserActivityLogResponse> response = getAllActivityLogsInternal(page, size);
            if (response.getContent().isEmpty()) {
                throw new NotFoundException("No activity logs found");
            }
            return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "User activity logs retrieved successfully", response));
        } catch (Exception e) {
            log.error("Error retrieving activity logs: ", e);
            throw e;
        }
    }

    @Override
    public ResponseEntity<ResponseObject> getActivityLogById(Long id) {
        try {
            UserActivityLogResponse response = getActivityLogByIdInternal(id);
            return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Activity log retrieved successfully", response));
        } catch (Exception e) {
            log.error("Error retrieving activity log by ID {}: ", id, e);
            throw e;
        }
    }

    @Override
    public ResponseEntity<ResponseObject> getActivityLogsByUserId(Long userId, int page, int size) {
        try {
            PageResponse<UserActivityLogResponse> response = getActivityLogsByUserIdInternal(userId, page, size);
            if (response.getContent().isEmpty()) {
                throw new NotFoundException("No activity logs found for user ID: " + userId);
            }
            return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Activity logs retrieved successfully", response));
        } catch (Exception e) {
            log.error("Error retrieving activity logs for user {}: ", userId, e);
            throw e;
        }
    }

    @Override
    public ResponseEntity<ResponseObject> getActivityLogsByType(String activityType, int page, int size) {
        try {
            PageResponse<UserActivityLogResponse> response = getActivityLogsByTypeInternal(activityType, page, size);
            if (response.getContent().isEmpty()) {
                throw new NotFoundException("No activity logs found for activity type: " + activityType);
            }
            return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Activity logs retrieved successfully", response));
        } catch (Exception e) {
            log.error("Error retrieving activity logs by type {}: ", activityType, e);
            throw e;
        }
    }

    @Override
    public ResponseEntity<ResponseObject> getActivityLogsByDateRange(LocalDateTime startTime, LocalDateTime endTime, int page, int size) {
        try {
            PageResponse<UserActivityLogResponse> response = getActivityLogsByDateRangeInternal(startTime, endTime, page, size);
            if (response.getContent().isEmpty()) {
                throw new NotFoundException("No activity logs found for the specified date range");
            }
            return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Activity logs retrieved successfully", response));
        } catch (Exception e) {
            log.error("Error retrieving activity logs by date range: ", e);
            throw e;
        }
    }

    @Override
    public ResponseEntity<ResponseObject> exportActivityLogs(UserActivityLogExportRequest request, int page, int size) {
        try {
            PageResponse<UserActivityLogResponse> response = exportActivityLogsInternal(request, page, size);
            if (response.getContent().isEmpty()) {
                throw new NotFoundException("No activity logs found for export in the specified date range");
            }
            return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Activity logs exported successfully", response));
        } catch (Exception e) {
            log.error("Error exporting activity logs: ", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> deleteActivityLog(Long id) {
        try {
            // Check if activity log exists before deleting
            userActivityLogRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Activity log not found with ID: " + id));
            
            deleteActivityLogInternal(id);
            return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Activity log deleted successfully", null));
        } catch (Exception e) {
            log.error("Error deleting activity log {}: ", id, e);
            throw e;
        }
    }

    // Internal business logic implementations
    @Override
    public PageResponse<UserActivityLogResponse> getAllActivityLogsInternal(int page, int size) {
        Pageable pageable = PageUtils.createPageable(
            PageUtils.normalizePageNumber(page), 
            PageUtils.normalizePageSize(size)
        );
        Page<UserActivityLog> logs = userActivityLogRepository.findAll(pageable);
        Page<UserActivityLogResponse> mappedLogs = logs.map(userActivityLogMapper::toResponse);
        return PageUtils.toPageResponse(mappedLogs);
    }

    @Override
    public UserActivityLogResponse getActivityLogByIdInternal(Long id) {
        UserActivityLog log = findById(id);
        return userActivityLogMapper.toResponse(log);
    }

    @Override
    public PageResponse<UserActivityLogResponse> getActivityLogsByUserIdInternal(Long userId, int page, int size) {
        Pageable pageable = PageUtils.createPageable(
            PageUtils.normalizePageNumber(page), 
            PageUtils.normalizePageSize(size)
        );
        Page<UserActivityLog> logs = userActivityLogRepository.findByUserIdOrderByTimestampDesc(userId, pageable);
        Page<UserActivityLogResponse> mappedLogs = logs.map(userActivityLogMapper::toResponse);
        return PageUtils.toPageResponse(mappedLogs);
    }

    @Override
    public PageResponse<UserActivityLogResponse> getActivityLogsByTypeInternal(String activityType, int page, int size) {
        try {
            ActivityType type = ActivityType.valueOf(activityType.toUpperCase());
            Pageable pageable = PageUtils.createPageable(
                PageUtils.normalizePageNumber(page), 
                PageUtils.normalizePageSize(size)
            );
            Page<UserActivityLog> logs = userActivityLogRepository.findByActivityTypeOrderByTimestampDesc(type, pageable);
            Page<UserActivityLogResponse> mappedLogs = logs.map(userActivityLogMapper::toResponse);
            return PageUtils.toPageResponse(mappedLogs);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid activity type: " + activityType);
        }
    }

    @Override
    public PageResponse<UserActivityLogResponse> getActivityLogsByDateRangeInternal(LocalDateTime startTime, LocalDateTime endTime, int page, int size) {
        Pageable pageable = PageUtils.createPageable(
            PageUtils.normalizePageNumber(page), 
            PageUtils.normalizePageSize(size)
        );
        Page<UserActivityLog> logs = userActivityLogRepository.findByTimestampBetween(startTime, endTime, pageable);
        Page<UserActivityLogResponse> mappedLogs = logs.map(userActivityLogMapper::toResponse);
        return PageUtils.toPageResponse(mappedLogs);
    }

    @Override
    public PageResponse<UserActivityLogResponse> searchActivityLogs(UserActivityLogFilterRequest request) {
        Pageable pageable = PageUtils.createPageable(
            PageUtils.normalizePageNumber(request.getPage()), 
            PageUtils.normalizePageSize(request.getSize())
        );
        Page<UserActivityLog> logs = userActivityLogRepository.findWithFilters(
                request.getUserId(),
                request.getActivityType(),
                request.getStatus(),
                request.getStartTime(),
                request.getEndTime(),
                pageable
        );
        Page<UserActivityLogResponse> mappedLogs = logs.map(userActivityLogMapper::toResponse);
        return PageUtils.toPageResponse(mappedLogs);
    }

    @Override
    @Transactional
    public void deleteActivityLogInternal(Long id) {
        deleteById(id);
    }

    @Override
    public Page<UserActivityLog> findAll(Pageable pageable) {
        return userActivityLogRepository.findAll(pageable);
    }

    @Override
    public UserActivityLog findById(Long id) {
        return userActivityLogRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("UserActivityLog not found with id: " + id));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!userActivityLogRepository.existsById(id)) {
            throw new NotFoundException("UserActivityLog not found with id: " + id);
        }
        userActivityLogRepository.deleteById(id);
        log.info("User activity log deleted: {}", id);
    }

    @Override
    public Page<UserActivityLog> findByUserId(Long userId, Pageable pageable) {
        return userActivityLogRepository.findByUserIdOrderByTimestampDesc(userId, pageable);
    }

    @Override
    public Page<UserActivityLog> findByActivityType(ActivityType activityType, Pageable pageable) {
        return userActivityLogRepository.findByActivityTypeOrderByTimestampDesc(activityType, pageable);
    }

    @Override
    public Page<UserActivityLog> findByStatus(String status, Pageable pageable) {
        return userActivityLogRepository.findByStatusOrderByTimestampDesc(status, pageable);
    }

    @Override
    public Page<UserActivityLog> findByDateRange(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        return userActivityLogRepository.findByTimestampBetween(startTime, endTime, pageable);
    }

    @Override
    public Page<UserActivityLog> findWithFilters(Long userId, ActivityType activityType, String status,
                                                LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        return userActivityLogRepository.findWithFilters(userId, activityType, status, startTime, endTime, pageable);
    }

    @Override
    public List<UserActivityLog> findByDateRangeForExport(LocalDateTime startTime, LocalDateTime endTime) {
        return userActivityLogRepository.findByTimestampBetween(startTime, endTime);
    }

    @Override
    public Map<ActivityType, Long> getActivityStats(LocalDateTime startTime) {
        List<Object[]> results = userActivityLogRepository.getActivityStatsSince(startTime);
        return results.stream()
                .collect(Collectors.toMap(
                    row -> (ActivityType) row[0],
                    row -> (Long) row[1]
                ));
    }

    @Override
    public Long getUserActivityCount(Long userId, LocalDateTime startTime) {
        return userActivityLogRepository.countUserActivitySince(userId, startTime);
    }

    @Override
    public PageResponse<UserActivityLogResponse> exportActivityLogsInternal(UserActivityLogExportRequest request, int page, int size) {
        // Convert LocalDate to LocalDateTime
        LocalDateTime startTime = request.getStartDate().atStartOfDay();
        LocalDateTime endTime = request.getEndDate().atTime(23, 59, 59);
        
        // Create pageable with default sorting by ID DESC
        Pageable pageable = PageUtils.createPageable(
            PageUtils.normalizePageNumber(page), 
            PageUtils.normalizePageSize(size)
        );
        
        // Fetch data from repository
        Page<UserActivityLog> logs = userActivityLogRepository.findByTimestampBetween(startTime, endTime, pageable);
        
        // Map to response DTOs
        Page<UserActivityLogResponse> mappedLogs = logs.map(userActivityLogMapper::toResponse);
        
        // Convert to PageResponse
        return PageUtils.toPageResponse(mappedLogs);
    }

    @Override
    @Transactional
    public void cleanupOldLogs(LocalDateTime cutoffDate) {
        // For now, just log the action. In production, you might want to implement batch deletion
        log.info("Cleanup old user activity logs before: {}", cutoffDate);
        // userActivityLogRepository.deleteByTimestampBefore(cutoffDate);
    }
}
