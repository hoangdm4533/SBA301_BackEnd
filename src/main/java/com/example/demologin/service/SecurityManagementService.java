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
    ResponseEntity<ResponseObject> unlockAccountById(Long userId);
    ResponseEntity<ResponseObject> lockAccountById(Long userId, BaseActionRequest request);
    ResponseEntity<ResponseObject> changeUserStatusById(Long userId, UserStatus status, BaseActionRequest request);
    ResponseEntity<ResponseObject> getAccountLockouts(int page, int size, boolean activeOnly);
    ResponseEntity<ResponseObject> getLoginAttemptsByUserId(Long userId, int page, int size, int hoursBack);
    ResponseEntity<ResponseObject> getLockoutStatusByUserId(Long userId);
}
