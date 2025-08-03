package com.example.demologin.serviceImpl;

import com.example.demologin.dto.response.PageResponse;
import com.example.demologin.dto.response.UserActionLogResponse;
import com.example.demologin.entity.UserActionLog;
import com.example.demologin.enums.UserActionType;
import com.example.demologin.exception.exceptions.NotFoundException;
import com.example.demologin.repository.UserActionLogRepository;
import com.example.demologin.service.UserActionLogService;
import com.example.demologin.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserActionLogServiceImpl implements UserActionLogService {
    
    private final UserActionLogRepository userActionLogRepository;
    
    @Override
    @Transactional
    public UserActionLog save(UserActionLog userActionLog) {
        return userActionLogRepository.save(userActionLog);
    }
    
    @Override
    @Transactional
    public void deleteOldLogs(LocalDateTime beforeDate) {
        List<UserActionLog> oldLogs = userActionLogRepository.findByActionTimeBetween(
            LocalDateTime.of(2000, 1, 1, 0, 0), beforeDate);
        userActionLogRepository.deleteAll(oldLogs);
    }

    @Override
    public PageResponse<UserActionLogResponse> getAllActionLogs(int page, int size) {
        Pageable pageable = PageUtils.createPageable(
            PageUtils.normalizePageNumber(page), 
            PageUtils.normalizePageSize(size)
        );
        Page<UserActionLog> actionLogs = userActionLogRepository.findAll(pageable);
        Page<UserActionLogResponse> mappedLogs = actionLogs.map(this::convertToResponse);
        PageResponse<UserActionLogResponse> response = PageUtils.toPageResponse(mappedLogs);
        
        if (response.getContent().isEmpty()) {
            throw new NotFoundException("No action logs found");
        }
        return response;
    }

    @Override
    public UserActionLogResponse getActionLogById(Long id) {
        UserActionLog actionLog = userActionLogRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Action log not found with id: " + id));
        return convertToResponse(actionLog);
    }

    @Override
    public PageResponse<UserActionLogResponse> getActionLogsByUserId(Long userId, int page, int size) {
        Pageable pageable = PageUtils.createPageable(
            PageUtils.normalizePageNumber(page), 
            PageUtils.normalizePageSize(size)
        );
        Page<UserActionLog> actionLogs = userActionLogRepository.findByUserIdOrderByActionTimeDesc(userId, pageable);
        Page<UserActionLogResponse> mappedLogs = actionLogs.map(this::convertToResponse);
        PageResponse<UserActionLogResponse> response = PageUtils.toPageResponse(mappedLogs);
        
        if (response.getContent().isEmpty()) {
            throw new NotFoundException("No action logs found for user ID: " + userId);
        }
        return response;
    }

    @Override
    public PageResponse<UserActionLogResponse> getActionLogsByActionType(String actionType, int page, int size) {
        try {
            UserActionType type = UserActionType.valueOf(actionType.toUpperCase());
            Pageable pageable = PageUtils.createPageable(
                PageUtils.normalizePageNumber(page), 
                PageUtils.normalizePageSize(size)
            );
            Page<UserActionLog> actionLogs = userActionLogRepository.findByActionTypeOrderByActionTimeDesc(type, pageable);
            Page<UserActionLogResponse> mappedLogs = actionLogs.map(this::convertToResponse);
            PageResponse<UserActionLogResponse> response = PageUtils.toPageResponse(mappedLogs);
            
            if (response.getContent().isEmpty()) {
                throw new NotFoundException("No action logs found for action type: " + actionType);
            }
            return response;
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Invalid action type: " + actionType);
        }
    }

    @Override
    public PageResponse<UserActionLogResponse> getActionLogsByTargetType(String targetType, int page, int size) {
        Pageable pageable = PageUtils.createPageable(
            PageUtils.normalizePageNumber(page), 
            PageUtils.normalizePageSize(size)
        );
        Page<UserActionLog> actionLogs = userActionLogRepository.findByTargetTypeOrderByActionTimeDesc(targetType, pageable);
        Page<UserActionLogResponse> mappedLogs = actionLogs.map(this::convertToResponse);
        PageResponse<UserActionLogResponse> response = PageUtils.toPageResponse(mappedLogs);
        
        if (response.getContent().isEmpty()) {
            throw new NotFoundException("No action logs found for target type: " + targetType);
        }
        return response;
    }

    @Override
    public PageResponse<UserActionLogResponse> getActionLogsByDateRange(String startDate, String endDate, int page, int size) {
        try {
            LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
            LocalDateTime endDateTime = LocalDate.parse(endDate).atTime(23, 59, 59);
            
            Pageable pageable = PageUtils.createPageable(
                PageUtils.normalizePageNumber(page), 
                PageUtils.normalizePageSize(size)
            );
            Page<UserActionLog> actionLogs = userActionLogRepository.findByActionTimeBetween(startDateTime, endDateTime, pageable);
            Page<UserActionLogResponse> mappedLogs = actionLogs.map(this::convertToResponse);
            PageResponse<UserActionLogResponse> response = PageUtils.toPageResponse(mappedLogs);
            
            if (response.getContent().isEmpty()) {
                throw new NotFoundException("No action logs found for the specified date range");
            }
            return response;
        } catch (Exception e) {
            throw new NotFoundException("Invalid date format. Use YYYY-MM-DD format.");
        }
    }

    @Override
    @Transactional
    public String deleteActionLog(Long id) {
        if (!userActionLogRepository.existsById(id)) {
            throw new NotFoundException("Action log not found with ID: " + id);
        }
        userActionLogRepository.deleteById(id);
        log.info("User action log deleted: {}", id);
        return "Action log deleted successfully";
    }

    private UserActionLogResponse convertToResponse(UserActionLog actionLog) {
        return UserActionLogResponse.builder()
                .id(actionLog.getId())
                .userId(actionLog.getUserId())
                .username(actionLog.getUsername())
                .roleName(actionLog.getRoleName())
                .actionType(actionLog.getActionType())
                .targetType(actionLog.getTargetType())
                .targetId(actionLog.getTargetId())
                .targetName(actionLog.getTargetName())
                .description(actionLog.getDescription())
                .reason(actionLog.getReason())
                .changeSummary(actionLog.getChangeSummary())
                .ipAddress(actionLog.getIpAddress())
                .userAgent(actionLog.getUserAgent())
                .actionTime(actionLog.getActionTime())
                .build();
    }
}
