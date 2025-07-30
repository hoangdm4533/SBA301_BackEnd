package com.example.demologin.service;

import com.example.demologin.entity.UserActionLog;
import com.example.demologin.enums.UserActionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface UserActionLogService {
    
    UserActionLog save(UserActionLog userActionLog);
    
    Page<UserActionLog> findAll(Pageable pageable);
    
    Page<UserActionLog> findByUserId(Long userId, Pageable pageable);
    
    Page<UserActionLog> findByUsername(String username, Pageable pageable);
    
    Page<UserActionLog> findByActionType(UserActionType actionType, Pageable pageable);
    
    Page<UserActionLog> findByTargetType(String targetType, Pageable pageable);
    
    List<UserActionLog> findByDateRange(LocalDateTime startTime, LocalDateTime endTime);
    
    Page<UserActionLog> findWithFilters(
        Long userId, 
        String username, 
        UserActionType actionType, 
        String targetType, 
        LocalDateTime startTime, 
        LocalDateTime endTime, 
        Pageable pageable
    );
    
    void deleteById(Long id);
    
    void deleteOldLogs(LocalDateTime beforeDate);
}
