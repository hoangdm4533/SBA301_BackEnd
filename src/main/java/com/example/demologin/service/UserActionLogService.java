package com.example.demologin.service;

import com.example.demologin.dto.response.PageResponse;
import com.example.demologin.dto.response.UserActionLogResponse;
import com.example.demologin.entity.UserActionLog;

import java.time.LocalDateTime;

public interface UserActionLogService {
    
    UserActionLog save(UserActionLog userActionLog);
    void deleteOldLogs(LocalDateTime beforeDate);
    
    PageResponse<UserActionLogResponse> getAllActionLogs(int page, int size);
    UserActionLogResponse getActionLogById(Long id);
    PageResponse<UserActionLogResponse> getActionLogsByUserId(Long userId, int page, int size);
    PageResponse<UserActionLogResponse> getActionLogsByActionType(String actionType, int page, int size);
    PageResponse<UserActionLogResponse> getActionLogsByTargetType(String targetType, int page, int size);
    PageResponse<UserActionLogResponse> getActionLogsByDateRange(String startDate, String endDate, int page, int size);
    String deleteActionLog(Long id);
}
