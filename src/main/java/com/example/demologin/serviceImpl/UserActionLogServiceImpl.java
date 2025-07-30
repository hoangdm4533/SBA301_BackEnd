package com.example.demologin.serviceImpl;

import com.example.demologin.entity.UserActionLog;
import com.example.demologin.enums.UserActionType;
import com.example.demologin.repository.UserActionLogRepository;
import com.example.demologin.service.UserActionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    @Transactional(readOnly = true)
    public Page<UserActionLog> findAll(Pageable pageable) {
        return userActionLogRepository.findAll(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserActionLog> findByUserId(Long userId, Pageable pageable) {
        return userActionLogRepository.findByUserIdOrderByActionTimeDesc(userId, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserActionLog> findByUsername(String username, Pageable pageable) {
        return userActionLogRepository.findByUsernameContainingIgnoreCaseOrderByActionTimeDesc(username, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserActionLog> findByActionType(UserActionType actionType, Pageable pageable) {
        return userActionLogRepository.findByActionTypeOrderByActionTimeDesc(actionType, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserActionLog> findByTargetType(String targetType, Pageable pageable) {
        return userActionLogRepository.findByTargetTypeOrderByActionTimeDesc(targetType, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserActionLog> findByDateRange(LocalDateTime startTime, LocalDateTime endTime) {
        return userActionLogRepository.findByActionTimeBetween(startTime, endTime);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserActionLog> findWithFilters(Long userId, String username, UserActionType actionType, 
                                             String targetType, LocalDateTime startTime, LocalDateTime endTime, 
                                             Pageable pageable) {
        return userActionLogRepository.findWithFilters(userId, username, actionType, targetType, 
                                                      startTime, endTime, pageable);
    }
    
    @Override
    public void deleteById(Long id) {
        userActionLogRepository.deleteById(id);
    }
    
    @Override
    public void deleteOldLogs(LocalDateTime beforeDate) {
        List<UserActionLog> oldLogs = userActionLogRepository.findByActionTimeBetween(
            LocalDateTime.of(2000, 1, 1, 0, 0), beforeDate);
        userActionLogRepository.deleteAll(oldLogs);
    }
}
