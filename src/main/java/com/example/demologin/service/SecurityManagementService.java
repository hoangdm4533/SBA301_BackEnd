package com.example.demologin.service;

import com.example.demologin.dto.request.BaseActionRequest;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.enums.UserStatus;
import org.springframework.http.ResponseEntity;

/**
 * Service for handling security management operations
 */
public interface SecurityManagementService {
    
    /**
     * Business logic methods (for controllers)
     */
    ResponseEntity<ResponseObject> unlockAccount(String username);
    ResponseEntity<ResponseObject> lockAccount(String username, BaseActionRequest request);
    ResponseEntity<ResponseObject> changeUserStatus(String username, UserStatus status, BaseActionRequest request);
    ResponseEntity<ResponseObject> getAccountLockouts(int page, int size, boolean activeOnly);
    ResponseEntity<ResponseObject> getLoginAttempts(String username, int page, int size, int hoursBack);
    ResponseEntity<ResponseObject> getLockoutStatus(String username);
    
}
