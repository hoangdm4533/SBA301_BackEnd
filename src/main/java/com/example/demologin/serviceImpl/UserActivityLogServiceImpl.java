package com.example.demologin.serviceImpl;

import com.example.demologin.dto.request.UserActivityLogExportRequest;
import com.example.demologin.dto.request.UserActivityLogFilterRequest;
import com.example.demologin.dto.response.PageResponse;
import com.example.demologin.dto.response.UserActivityLogResponse;
import com.example.demologin.entity.UserActivityLog;
import com.example.demologin.enums.ActivityType;
import com.example.demologin.exception.exceptions.ResourceNotFoundException;
import com.example.demologin.mapper.UserActivityLogMapper;
import com.example.demologin.repository.UserActivityLogRepository;
import com.example.demologin.service.UserActivityLogService;
import com.example.demologin.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Override
    public PageResponse<UserActivityLogResponse> getAllActivityLogs(int page, int size) {
        Pageable pageable = PageUtils.createPageable(
            PageUtils.normalizePageNumber(page), 
            PageUtils.normalizePageSize(size)
        );
        Page<UserActivityLog> logs = userActivityLogRepository.findAll(pageable);
        Page<UserActivityLogResponse> mappedLogs = logs.map(userActivityLogMapper::toResponse);
        return PageUtils.toPageResponse(mappedLogs);
    }

    @Override
    public UserActivityLogResponse getActivityLogById(Long id) {
        UserActivityLog log = findById(id);
        return userActivityLogMapper.toResponse(log);
    }

    @Override
    public PageResponse<UserActivityLogResponse> getActivityLogsByUserId(Long userId, int page, int size) {
        Pageable pageable = PageUtils.createPageable(
            PageUtils.normalizePageNumber(page), 
            PageUtils.normalizePageSize(size)
        );
        Page<UserActivityLog> logs = userActivityLogRepository.findByUserIdOrderByTimestampDesc(userId, pageable);
        Page<UserActivityLogResponse> mappedLogs = logs.map(userActivityLogMapper::toResponse);
        return PageUtils.toPageResponse(mappedLogs);
    }

    @Override
    public PageResponse<UserActivityLogResponse> getActivityLogsByType(String activityType, int page, int size) {
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
    public PageResponse<UserActivityLogResponse> getActivityLogsByDateRange(LocalDateTime startTime, LocalDateTime endTime, int page, int size) {
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
    public void deleteActivityLog(Long id) {
        deleteById(id);
    }

    @Override
    public Page<UserActivityLog> findAll(Pageable pageable) {
        return userActivityLogRepository.findAll(pageable);
    }

    @Override
    public UserActivityLog findById(Long id) {
        return userActivityLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserActivityLog not found with id: " + id));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!userActivityLogRepository.existsById(id)) {
            throw new ResourceNotFoundException("UserActivityLog not found with id: " + id);
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
    public PageResponse<UserActivityLogResponse> exportActivityLogs(UserActivityLogExportRequest request, int page, int size) {
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
