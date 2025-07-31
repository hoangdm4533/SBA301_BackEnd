package com.example.demologin.serviceImpl;

import com.example.demologin.dto.request.BaseActionRequest;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.entity.User;
import com.example.demologin.exception.exceptions.NotFoundException;
import com.example.demologin.repository.UserRepository;
import com.example.demologin.service.SessionManagementService;
import com.example.demologin.service.TokenVersionService;
import com.example.demologin.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionManagementServiceImpl implements SessionManagementService {
    
    private final TokenVersionService tokenVersionService;
    private final UserRepository userRepository;
    
    // Business logic methods for controllers
    @Override
    public ResponseEntity<ResponseObject> logoutCurrentDevice() {
        User currentUser = AccountUtils.getCurrentUser();
        String message = logoutCurrentDevice(currentUser);
        
        Map<String, Object> data = Map.of(
            "message", message,
            "username", currentUser.getUsername(),
            "logoutType", "current_device",
            "timestamp", LocalDateTime.now()
        );
        
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Logout successful", data));
    }

    @Override
    public ResponseEntity<ResponseObject> logoutFromAllDevices(BaseActionRequest request) {
        User currentUser = AccountUtils.getCurrentUser();
        String message = logoutFromAllDevices(currentUser, request);
        
        Map<String, Object> data = Map.of(
            "message", message,
            "username", currentUser.getUsername(),
            "logoutType", "all_devices",
            "reason", request.getReason(),
            "timestamp", LocalDateTime.now()
        );
        
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Logout from all devices successful", data));
    }
    
    @Override
    public ResponseEntity<ResponseObject> forceLogoutUser(Long userId, BaseActionRequest request) {
        String message = forceLogoutFromAllDevices(userId, request);
        User adminUser = AccountUtils.getCurrentUser();
        
        Map<String, Object> data = Map.of(
            "message", message,
            "targetUserId", userId,
            "logoutType", "admin_force_logout",
            "adminUser", adminUser.getUsername(),
            "reason", request.getReason(),
            "timestamp", LocalDateTime.now()
        );
        
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Force logout successful", data));
    }
    
    @Override
    public ResponseEntity<ResponseObject> getActiveSessionCount() {
        User currentUser = AccountUtils.getCurrentUser();
        int sessionCount = getEstimatedActiveSessionCount(currentUser.getUserId());
        boolean hasActiveSessions = hasActiveSessions(currentUser.getUserId());
        
        Map<String, Object> data = Map.of(
            "username", currentUser.getUsername(),
            "estimatedActiveSessionCount", sessionCount,
            "hasActiveSessions", hasActiveSessions,
            "tokenVersion", currentUser.getTokenVersion(),
            "note", "This is an estimation based on token version. For accurate session tracking, implement proper session store."
        );
        
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Session count retrieved successfully", data));
    }
    
    @Override
    public ResponseEntity<ResponseObject> getUserSessionStatus(Long userId) {
        int sessionCount = getEstimatedActiveSessionCount(userId);
        boolean hasActiveSessions = hasActiveSessions(userId);
        
        Map<String, Object> data = Map.of(
            "userId", userId,
            "estimatedActiveSessionCount", sessionCount,
            "hasActiveSessions", hasActiveSessions,
            "note", "This is an estimation based on token version. For accurate session tracking, implement proper session store."
        );
        
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "User session count retrieved successfully", data));
    }
    
    // Raw service methods for internal use
    @Override
    @Transactional
    public String logoutCurrentDevice(User user) {
        log.info("User {} logging out from current device", user.getUsername());
        
        // Invalidate current token by incrementing token version
        tokenVersionService.incrementTokenVersionByUserId(user.getUserId());
        
        return String.format("User %s has been logged out from current device", user.getUsername());
    }
    
    @Override
    @Transactional
    public String logoutFromAllDevices(User user, BaseActionRequest request) {
        log.info("User {} logging out from all devices. Reason: {}", user.getUsername(), request.getReason());
        
        // Invalidate all tokens by incrementing token version
        tokenVersionService.incrementTokenVersionByUserId(user.getUserId());
        
        return String.format("User %s has been logged out from all devices", user.getUsername());
    }
    
    @Override
    @Transactional
    public String logoutFromAllDevices(Long userId, BaseActionRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        
        return logoutFromAllDevices(user, request);
    }
    
    @Override
    @Transactional
    public String forceLogoutFromAllDevices(Long userId, BaseActionRequest request) {
        User targetUser = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        
        User adminUser = AccountUtils.getCurrentUser();
        log.info("Admin {} force logging out user {} from all devices. Reason: {}", 
                adminUser.getUsername(), targetUser.getUsername(), request.getReason());
        
        // Invalidate all tokens for target user
        tokenVersionService.incrementTokenVersionByUserId(userId);
        
        return String.format("User %s has been force logged out from all devices by admin %s", 
                           targetUser.getUsername(), adminUser.getUsername());
    }
    
    @Override
    @Transactional(readOnly = true)
    public int getEstimatedActiveSessionCount(Long userId) {
        // This is a simple estimation based on token version
        // In a real-world scenario, you might want to track active sessions in Redis or database
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        
        // Simple logic: if token version is 0, likely no active sessions beyond current
        // If token version > 0, there might be sessions that were invalidated
        // This is a basic estimation - for accurate session tracking, implement proper session store
        return user.getTokenVersion() == 0 ? 1 : 0;
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasActiveSessions(Long userId) {
        // Basic check - in real implementation, check against session store
        return getEstimatedActiveSessionCount(userId) > 0;
    }
}
