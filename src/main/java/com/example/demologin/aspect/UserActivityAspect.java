package com.example.demologin.aspect;

import com.example.demologin.annotation.UserActivity;
import com.example.demologin.entity.UserActivityLog;
import com.example.demologin.entity.User;
import com.example.demologin.repository.UserActivityLogRepository;
import com.example.demologin.repository.UserRepository;
import com.example.demologin.utils.AccountUtils;
import com.example.demologin.utils.IpUtils;
import com.example.demologin.utils.LocationUtil;
import com.example.demologin.utils.UserAgentUtil;
import com.example.demologin.dto.request.login.LoginRequest;
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
    private final UserRepository userRepository;
    
    @AfterReturning(value = "@annotation(userActivity)", returning = "result")
    public void logUserActivity(JoinPoint joinPoint, UserActivity userActivity, Object result) {
        try {
            User currentUser = null;
            try {
                currentUser = AccountUtils.getCurrentUser();
            } catch (Exception e) {
                log.debug("Could not get current user from context: {}", e.getMessage());
            }
            
            // For login endpoint, extract user info from request if context fails
            if (currentUser == null && "LOGIN_ATTEMPT".equals(userActivity.activityType().name())) {
                currentUser = extractUserFromLoginRequest(joinPoint, result);
            }
            
            // Get client IP address and user agent
            String clientIp = "unknown";
            String userAgent = "unknown";
            try {
                String rawIp = IpUtils.getClientIpAddress();
                // Format IP address with localhost info
                if (rawIp != null && (rawIp.equals("127.0.0.1") || rawIp.equals("::1"))) {
                    clientIp = rawIp + " (localhost)";
                } else {
                    clientIp = rawIp != null ? rawIp : "unknown";
                }
                userAgent = IpUtils.getUserAgent();
            } catch (Exception e) {
                log.debug("Could not get client info: {}", e.getMessage());
            }
            
            // Parse device information from user agent
            UserAgentUtil.DeviceInfo deviceInfo = UserAgentUtil.parseUserAgent(userAgent);
            
            // Get location information from IP (use raw IP without localhost text)
            String ipForLocation = clientIp.contains("(localhost)") ? 
                clientIp.substring(0, clientIp.indexOf(" (localhost)")) : clientIp;
            LocationUtil.LocationInfo locationInfo = LocationUtil.getLocationFromIP(ipForLocation);
            
            // Prepare activity log data
            Long userId = userActivity.logUserId() && currentUser != null ? currentUser.getUserId() : null;
            String fullName = userActivity.logUserId() && currentUser != null ? currentUser.getFullName() : null;
            String status = "SUCCESS";
            String details = userActivity.details().isEmpty() ? 
                String.format("%s - %s", userActivity.activityType(), joinPoint.getSignature().getName()) : 
                userActivity.details();
            
            // Check for existing log with same key parameters
            UserActivityLog existingLog = null;
            if (userId != null) {
                existingLog = userActivityLogRepository.findTopByUserIdAndActivityTypeAndIpAddressAndStatusAndUserAgentOrderByTimestampDesc(
                    userId, userActivity.activityType(), clientIp, status, userAgent);
            }
            
            UserActivityLog activityLog;
            if (existingLog != null) {
                // Update existing log instead of creating new one
                existingLog.setTimestamp(LocalDateTime.now());
                existingLog.setFullName(fullName); // Update fullName in case it changed
                existingLog.setDetails(details);
                // Update device info in case browser/OS updated
                existingLog.setBrowser(deviceInfo.getBrowser());
                existingLog.setBrowserVersion(deviceInfo.getBrowserVersion());
                existingLog.setOperatingSystem(deviceInfo.getOperatingSystem());
                existingLog.setDevice(deviceInfo.getDevice());
                existingLog.setDeviceType(deviceInfo.getDeviceType());
                // Update location info
                existingLog.setCity(locationInfo.getCity());
                existingLog.setRegion(locationInfo.getRegion());
                existingLog.setCountry(locationInfo.getCountry());
                existingLog.setCountryCode(locationInfo.getCountryCode());
                
                activityLog = existingLog;
                log.debug("Updated existing user activity log for user {} from {}", fullName, clientIp);
            } else {
                // Create new log
                activityLog = UserActivityLog.builder()
                    .activityType(userActivity.activityType())
                    .userId(userId)
                    .fullName(fullName)
                    .timestamp(LocalDateTime.now())
                    .status(status)
                    .details(details)
                    .ipAddress(clientIp)
                    .userAgent(userAgent)
                    // Device information
                    .browser(deviceInfo.getBrowser())
                    .browserVersion(deviceInfo.getBrowserVersion())
                    .operatingSystem(deviceInfo.getOperatingSystem())
                    .device(deviceInfo.getDevice())
                    .deviceType(deviceInfo.getDeviceType())
                    // Location information
                    .city(locationInfo.getCity())
                    .region(locationInfo.getRegion())
                    .country(locationInfo.getCountry())
                    .countryCode(locationInfo.getCountryCode())
                    .build();
                log.debug("Created new user activity log for user {} from {}", fullName, clientIp);
            }
            
            userActivityLogRepository.save(activityLog);
            log.debug("User activity logged: {} for user {} from {} using {}", 
                userActivity.activityType(), 
                fullName != null ? fullName : "anonymous", 
                LocationUtil.formatLocationInfo(locationInfo),
                UserAgentUtil.formatDeviceInfo(deviceInfo));
            
        } catch (Exception e) {
            log.error("Failed to log user activity for method {}: {}", joinPoint.getSignature().getName(), e.getMessage());
        }
    }
    
    private User extractUserFromLoginRequest(JoinPoint joinPoint, Object result) {
        try {
            // Extract username from login request arguments
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (arg instanceof LoginRequest) {
                    LoginRequest loginRequest = (LoginRequest) arg;
                    String username = loginRequest.getUsername();
                    
                    // Find user by username if login was successful
                    if (isLoginSuccessful(result)) {
                        return userRepository.findByUsername(username).orElse(null);
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Could not extract user from login request: {}", e.getMessage());
        }
        return null;
    }
    
    private boolean isLoginSuccessful(Object result) {
        try {
            if (result instanceof org.springframework.http.ResponseEntity) {
                org.springframework.http.ResponseEntity<?> responseEntity = (org.springframework.http.ResponseEntity<?>) result;
                return responseEntity.getStatusCode().is2xxSuccessful();
            }
        } catch (Exception e) {
            log.debug("Could not determine login success: {}", e.getMessage());
        }
        return false;
    }
}
