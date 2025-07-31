package com.example.demologin.aspect;

import com.example.demologin.annotation.UserActivity;
import com.example.demologin.entity.UserActivityLog;
import com.example.demologin.entity.User;
import com.example.demologin.repository.UserActivityLogRepository;
import com.example.demologin.utils.AccountUtils;
import com.example.demologin.utils.IpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class UserActivityAspect {
    
    private final UserActivityLogRepository userActivityLogRepository;
    
    @AfterReturning("@annotation(userActivity)")
    public void logUserActivity(JoinPoint joinPoint, UserActivity userActivity) {
        try {
            User currentUser = null;
            try {
                currentUser = AccountUtils.getCurrentUser();
            } catch (Exception e) {
                log.warn("Could not get current user for activity logging: {}", e.getMessage());
            }
            
            // Get client IP address
            String clientIp = "unknown";
            String userAgent = "unknown";
            try {
                clientIp = IpUtils.getClientIpAddress();
                userAgent = IpUtils.getUserAgent();
            } catch (Exception e) {
                log.debug("Could not get client info: {}", e.getMessage());
            }
            
            UserActivityLog activityLog = UserActivityLog.builder()
                .activityType(userActivity.activityType())
                .userId(userActivity.logUserId() && currentUser != null ? currentUser.getUserId() : null)
                .username(userActivity.logUserId() && currentUser != null ? currentUser.getUsername() : null)
                .editorId(userActivity.logEditorId() && currentUser != null ? currentUser.getUserId() : null)
                .editorUsername(userActivity.logEditorId() && currentUser != null ? currentUser.getUsername() : null)
                .timestamp(LocalDateTime.now())
                .status("SUCCESS")
                .details(userActivity.details().isEmpty() ? 
                    String.format("%s - %s", userActivity.activityType(), joinPoint.getSignature().getName()) : 
                    userActivity.details())
                .ipAddress(clientIp)
                .userAgent(userAgent)
                .build();
            
            userActivityLogRepository.save(activityLog);
            log.debug("User activity logged: {} for user {} from IP {}", userActivity.activityType(), 
                currentUser != null ? currentUser.getUsername() : "anonymous", clientIp);
            
        } catch (Exception e) {
            log.error("Failed to log user activity for method {}: {}", joinPoint.getSignature().getName(), e.getMessage());
        }
    }
}
