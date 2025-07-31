package com.example.demologin.serviceImpl;

import com.example.demologin.dto.request.userActionLog.*;
import com.example.demologin.dto.response.PageResponse;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.dto.response.UserActionLogResponse;
import com.example.demologin.entity.UserActionLog;
import com.example.demologin.enums.UserActionType;
import com.example.demologin.exception.exceptions.NotFoundException;
import com.example.demologin.exception.exceptions.ValidationException;
import com.example.demologin.repository.UserActionLogRepository;
import com.example.demologin.service.UserActionLogService;
import com.example.demologin.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserActionLogServiceImpl implements UserActionLogService {
    
    private final UserActionLogRepository userActionLogRepository;
    
    @Override
    public UserActionLog save(UserActionLog userActionLog) {
        return userActionLogRepository.save(userActionLog);
    }
    
    @Override
    public void deleteOldLogs(LocalDateTime beforeDate) {
        List<UserActionLog> oldLogs = userActionLogRepository.findByActionTimeBetween(
            LocalDateTime.of(2000, 1, 1, 0, 0), beforeDate);
        userActionLogRepository.deleteAll(oldLogs);
    }

    // Controller methods implementation - return ResponseEntity<ResponseObject>
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseObject> getAllActionLogs(int page, int size) {
        page = PageUtils.normalizePageNumber(page);
        size = PageUtils.normalizePageSize(size);
        
        Pageable pageable = PageUtils.createPageable(page, size);
        Page<UserActionLog> actionLogs = userActionLogRepository.findAll(pageable);
        
        if (actionLogs.isEmpty()) {
            throw new NotFoundException("No action logs found");
        }
        
        PageResponse<UserActionLogResponse> pageResponse = PageUtils.toPageResponse(actionLogs.map(this::convertToResponse));
        ResponseObject response = new ResponseObject(HttpStatus.OK.value(), "User action logs retrieved successfully", pageResponse);
        return ResponseEntity.ok(response);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseObject> getActionLogById(GetActionLogByIdRequest request) {
        UserActionLog actionLog = userActionLogRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Action log not found with id: " + request.getId()));
        
        UserActionLogResponse response = convertToResponse(actionLog);
        ResponseObject responseObject = new ResponseObject(HttpStatus.OK.value(), "Action log retrieved successfully", response);
        return ResponseEntity.ok(responseObject);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseObject> getActionLogsByUserId(GetActionLogsByUserIdRequest request) {
        int page = PageUtils.normalizePageNumber(request.getPage());
        int size = PageUtils.normalizePageSize(request.getSize());
        
        Pageable pageable = PageUtils.createPageable(page, size);
        Page<UserActionLog> actionLogs = userActionLogRepository.findByUserIdOrderByActionTimeDesc(request.getUserId(), pageable);
        
        if (actionLogs.isEmpty()) {
            throw new NotFoundException("No action logs found for user ID: " + request.getUserId());
        }
        
        PageResponse<UserActionLogResponse> pageResponse = PageUtils.toPageResponse(actionLogs.map(this::convertToResponse));
        ResponseObject responseObject = new ResponseObject(HttpStatus.OK.value(), "Action logs retrieved successfully", pageResponse);
        return ResponseEntity.ok(responseObject);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseObject> getActionLogsByActionType(GetActionLogsByTypeRequest request) {
        try {
            int page = PageUtils.normalizePageNumber(request.getPage());
            int size = PageUtils.normalizePageSize(request.getSize());
            
            Pageable pageable = PageUtils.createPageable(page, size);
            UserActionType actionTypeEnum = UserActionType.valueOf(request.getActionType().toUpperCase());
            Page<UserActionLog> actionLogs = userActionLogRepository.findByActionTypeOrderByActionTimeDesc(actionTypeEnum, pageable);
            
            if (actionLogs.isEmpty()) {
                throw new NotFoundException("No action logs found for action type: " + request.getActionType());
            }
            
            PageResponse<UserActionLogResponse> pageResponse = PageUtils.toPageResponse(actionLogs.map(this::convertToResponse));
            ResponseObject responseObject = new ResponseObject(HttpStatus.OK.value(), "Action logs retrieved successfully", pageResponse);
            return ResponseEntity.ok(responseObject);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid action type: " + request.getActionType());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseObject> getActionLogsByTargetType(GetActionLogsByTargetTypeRequest request) {
        int page = PageUtils.normalizePageNumber(request.getPage());
        int size = PageUtils.normalizePageSize(request.getSize());
        
        Pageable pageable = PageUtils.createPageable(page, size);
        Page<UserActionLog> actionLogs = userActionLogRepository.findByTargetTypeOrderByActionTimeDesc(request.getTargetType(), pageable);
        
        if (actionLogs.isEmpty()) {
            throw new NotFoundException("No action logs found for target type: " + request.getTargetType());
        }
        
        PageResponse<UserActionLogResponse> pageResponse = PageUtils.toPageResponse(actionLogs.map(this::convertToResponse));
        ResponseObject responseObject = new ResponseObject(HttpStatus.OK.value(), "Action logs retrieved successfully", pageResponse);
        return ResponseEntity.ok(responseObject);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseObject> getActionLogsByDateRange(GetActionLogsByDateRangeRequest request) {
        try {
            LocalDateTime startDateTime = LocalDate.parse(request.getStartDate()).atStartOfDay();
            LocalDateTime endDateTime = LocalDate.parse(request.getEndDate()).atTime(23, 59, 59);
            
            if (startDateTime.isAfter(endDateTime)) {
                throw new ValidationException("Start date must be before or equal to end date");
            }
            
            int page = PageUtils.normalizePageNumber(request.getPage());
            int size = PageUtils.normalizePageSize(request.getSize());
            
            List<UserActionLog> actionLogs = userActionLogRepository.findByActionTimeBetween(startDateTime, endDateTime);
            
            if (actionLogs.isEmpty()) {
                throw new NotFoundException("No action logs found for the specified date range");
            }
            
            // Manual pagination for list
            int start = page * size;
            int end = Math.min(start + size, actionLogs.size());
            List<UserActionLog> paginatedLogs = actionLogs.subList(start, end);
            
            List<UserActionLogResponse> responseList = paginatedLogs.stream()
                    .map(this::convertToResponse)
                    .toList();
            
            PageResponse<UserActionLogResponse> pageResponse = new PageResponse<>(
                    responseList,
                    page,
                    size,
                    (long) actionLogs.size(),
                    (int) Math.ceil((double) actionLogs.size() / size),
                    end >= actionLogs.size()
            );
            
            ResponseObject responseObject = new ResponseObject(HttpStatus.OK.value(), "Action logs retrieved successfully", pageResponse);
            return ResponseEntity.ok(responseObject);
        } catch (DateTimeParseException e) {
            throw new ValidationException("Invalid date format. Use YYYY-MM-DD");
        }
    }

    @Override
    public ResponseEntity<ResponseObject> deleteActionLog(DeleteActionLogRequest request) {
        if (!userActionLogRepository.existsById(request.getId())) {
            throw new NotFoundException("Action log not found with id: " + request.getId());
        }
        
        userActionLogRepository.deleteById(request.getId());
        ResponseObject responseObject = new ResponseObject(HttpStatus.OK.value(), "Action log deleted successfully", null);
        return ResponseEntity.ok(responseObject);
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
