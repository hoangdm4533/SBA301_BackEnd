package com.example.demologin.service;

import com.example.demologin.dto.request.BaseActionRequest;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.enums.UserStatus;

/**
 * Service for handling security management operations
 */
public interface SecurityManagementService {
    
    /**
     * Business logic methods (for controllers)
     */
    ResponseObject unlockAccount(String username);
    ResponseObject lockAccount(String username, BaseActionRequest request);
    ResponseObject changeUserStatus(String username, UserStatus status, BaseActionRequest request);
    ResponseObject getAccountLockouts(int page, int size, boolean activeOnly);
    ResponseObject getLoginAttempts(String username, int page, int size, int hoursBack);
    ResponseObject getLockoutStatus(String username);
    
}
