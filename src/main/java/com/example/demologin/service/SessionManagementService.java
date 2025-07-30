package com.example.demologin.service;

import com.example.demologin.dto.request.BaseActionRequest;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.entity.User;

/**
 * Service for handling user session management and device logout
 */
public interface SessionManagementService {
    
    /**
     * Business logic methods (for controllers)
     */
    ResponseObject logoutCurrentDevice();
    ResponseObject logoutFromAllDevices(BaseActionRequest request);
    ResponseObject forceLogoutUser(Long userId, BaseActionRequest request);
    ResponseObject getActiveSessionCount();
    ResponseObject getUserSessionStatus(Long userId);
    
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
