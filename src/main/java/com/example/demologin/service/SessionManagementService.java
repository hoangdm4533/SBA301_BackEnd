package com.example.demologin.service;

import com.example.demologin.dto.request.BaseActionRequest;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.entity.User;
import org.springframework.http.ResponseEntity;

/**
 * Service for handling user session management and device logout
 */
public interface SessionManagementService {
    
    /**
     * Business logic methods (for controllers)
     */
    ResponseEntity<ResponseObject> logoutCurrentDevice();
    ResponseEntity<ResponseObject> logoutFromAllDevices(BaseActionRequest request);
    ResponseEntity<ResponseObject> forceLogoutUser(Long userId, BaseActionRequest request);
    ResponseEntity<ResponseObject> getActiveSessionCount();
    ResponseEntity<ResponseObject> getUserSessionStatus(Long userId);
    
    /**
     * Raw service methods (for internal use)
     */
    String logoutCurrentDevice(User user);
    String logoutFromAllDevices(User user, BaseActionRequest request);
    String logoutFromAllDevices(Long userId, BaseActionRequest request);
    String forceLogoutFromAllDevices(Long userId, BaseActionRequest request);
    int getEstimatedActiveSessionCount(Long userId);
    boolean hasActiveSessions(Long userId);
}
