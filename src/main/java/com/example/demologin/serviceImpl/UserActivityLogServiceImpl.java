package com.example.demologin.serviceImpl;

import com.example.demologin.dto.request.userActivityLog.UserActivityLogExportRequest;
import com.example.demologin.dto.request.userActivityLog.UserActivityLogFilterRequest;
import com.example.demologin.dto.response.PageResponse;
import com.example.demologin.dto.response.UserActivityLogResponse;
import com.example.demologin.entity.UserActivityLog;
import com.example.demologin.entity.User;
import com.example.demologin.enums.ActivityType;
import com.example.demologin.exception.exceptions.NotFoundException;
import com.example.demologin.mapper.UserActivityLogMapper;
import com.example.demologin.repository.UserActivityLogRepository;
import com.example.demologin.service.UserActivityLogService;
import com.example.demologin.utils.PageUtils;
import com.example.demologin.utils.IpUtils;
import com.example.demologin.utils.LocationUtil;
import com.example.demologin.utils.UserAgentUtil;
import com.example.demologin.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserActivityLogServiceImpl implements UserActivityLogService {

    private final UserActivityLogRepository userActivityLogRepository;
    private final UserActivityLogMapper userActivityLogMapper;

    @Override
    @Transactional
    public void logUserActivity(User user, ActivityType activityType, String details) {
        try {
            if (user == null) {
                log.warn("Cannot log activity - user is null");
                return;
            }
            
            String clientIp = IpUtils.getClientIpAddress();
            String userAgent = IpUtils.getUserAgent();
            UserAgentUtil.DeviceInfo deviceInfo = UserAgentUtil.parseUserAgent(userAgent);
            LocationUtil.LocationInfo locationInfo = LocationUtil.getLocationFromIP(clientIp);

            // Check if there's an existing log with same user, activity type, IP and device
            UserActivityLog existingLog = userActivityLogRepository
                .findTopByUserIdAndActivityTypeAndIpAddressAndUserAgentOrderByTimestampDesc(
                    user.getUserId(), activityType, clientIp, userAgent);

            if (existingLog != null) {
                // Update existing log timestamp if found within last 24 hours
                LocalDateTime dayAgo = LocalDateTime.now().minusDays(1);
                if (existingLog.getTimestamp().isAfter(dayAgo)) {
                    existingLog.setTimestamp(LocalDateTime.now());
                    existingLog.setDetails(details);
                    userActivityLogRepository.save(existingLog);
                    return;
                }
            }

            // Create new log entry
            UserActivityLog activityLog = UserActivityLog.builder()
                .activityType(activityType)
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .timestamp(LocalDateTime.now())
                .status("SUCCESS")
                .details(details)
                .ipAddress(clientIp)
                .userAgent(userAgent)
                .browser(deviceInfo.getBrowser())
                .browserVersion(deviceInfo.getBrowserVersion())
                .operatingSystem(deviceInfo.getOperatingSystem())
                .device(deviceInfo.getDevice())
                .deviceType(deviceInfo.getDeviceType())
                .city(locationInfo.getCity())
                .region(locationInfo.getRegion())
                .country(locationInfo.getCountry())
                .countryCode(locationInfo.getCountryCode())
                .build();

            userActivityLogRepository.save(activityLog);

        } catch (Exception e) {
            log.error("Failed to log user activity: {}", e.getMessage());
        }
    }

    @Override
    public void logUserActivity(ActivityType activityType, String details) {
        User currentUser = AccountUtils.getCurrentUser();
        logUserActivity(currentUser, activityType, details);
    }

    // Controller endpoints - return data only
    @Override
    public PageResponse<UserActivityLogResponse> getAllActivityLogs(int page, int size) {
        PageResponse<UserActivityLogResponse> response = getAllActivityLogsInternal(page, size);
        if (response.getContent().isEmpty()) {
            throw new NotFoundException("No activity logs found");
        }
        return response;
    }

    @Override
    public UserActivityLogResponse getActivityLogById(Long id) {
        return getActivityLogByIdInternal(id);
    }

    @Override
    public PageResponse<UserActivityLogResponse> getActivityLogsByUserId(Long userId, int page, int size) {
        PageResponse<UserActivityLogResponse> response = getActivityLogsByUserIdInternal(userId, page, size);
        if (response.getContent().isEmpty()) {
            throw new NotFoundException("No activity logs found for user ID: " + userId);
        }
        return response;
    }

    @Override
    public PageResponse<UserActivityLogResponse> getActivityLogsByType(String activityType, int page, int size) {
        PageResponse<UserActivityLogResponse> response = getActivityLogsByTypeInternal(activityType, page, size);
        if (response.getContent().isEmpty()) {
            throw new NotFoundException("No activity logs found for activity type: " + activityType);
        }
        return response;
    }

    @Override
    public PageResponse<UserActivityLogResponse> getActivityLogsByDateRange(LocalDateTime startTime, LocalDateTime endTime, int page, int size) {
        PageResponse<UserActivityLogResponse> response = getActivityLogsByDateRangeInternal(startTime, endTime, page, size);
        if (response.getContent().isEmpty()) {
            throw new NotFoundException("No activity logs found for the specified date range");
        }
        return response;
    }

    @Override
    public PageResponse<UserActivityLogResponse> exportActivityLogs(UserActivityLogExportRequest request, int page, int size) {
        PageResponse<UserActivityLogResponse> response = exportActivityLogsInternal(request, page, size);
        if (response.getContent().isEmpty()) {
            throw new NotFoundException("No activity logs found for export in the specified date range");
        }
        return response;
    }

    @Override
    @Transactional
    public String deleteActivityLog(Long id) {
        userActivityLogRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Activity log not found with ID: " + id));
        
        deleteActivityLogInternal(id);
        return "Activity log deleted successfully";
    }

    @Override
    public PageResponse<UserActivityLogResponse> getMyLoginHistory(int page, int size) {
        User currentUser = AccountUtils.getCurrentUser();
        if (currentUser == null) {
            throw new NotFoundException("User not authenticated");
        }
        
        Pageable pageable = PageUtils.createPageable(page, size);
        Page<UserActivityLog> activityLogs = userActivityLogRepository.findByUserIdAndActivityTypeOrderByTimestampDesc(
            currentUser.getUserId(), ActivityType.LOGIN_ATTEMPT, pageable);
        
        Page<UserActivityLogResponse> responsePage = activityLogs.map(userActivityLogMapper::toResponse);
        PageResponse<UserActivityLogResponse> pageResponse = new PageResponse<>(
            responsePage.getContent(),
            responsePage.getNumber(),
            responsePage.getSize(),
            responsePage.getTotalElements(),
            responsePage.getTotalPages(),
            responsePage.isLast()
        );
        
        if (pageResponse.getContent().isEmpty()) {
            throw new NotFoundException("No login history found for current user");
        }
        
        return pageResponse;
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
            throw new NotFoundException("Invalid activity type: " + activityType);
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
    public PageResponse<UserActivityLogResponse> exportActivityLogsInternal(UserActivityLogExportRequest request, int page, int size) {
        LocalDateTime startTime = request.getStartDate().atStartOfDay();
        LocalDateTime endTime = request.getEndDate().atTime(23, 59, 59);
        
        Pageable pageable = PageUtils.createPageable(
            PageUtils.normalizePageNumber(page), 
            PageUtils.normalizePageSize(size)
        );
        
        Page<UserActivityLog> logs = userActivityLogRepository.findByTimestampBetween(startTime, endTime, pageable);
        Page<UserActivityLogResponse> mappedLogs = logs.map(userActivityLogMapper::toResponse);
        
        return PageUtils.toPageResponse(mappedLogs);
    }

    @Override
    @Transactional
    public void cleanupOldLogs(LocalDateTime cutoffDate) {
        log.info("Cleanup old user activity logs before: {}", cutoffDate);
    }
}
