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
     * Business logic methods (for controllers) - Using userId for consistency
     */
    ResponseEntity<ResponseObject> unlockAccountById(Long userId);
    ResponseEntity<ResponseObject> lockAccountById(Long userId, BaseActionRequest request);
    ResponseEntity<ResponseObject> changeUserStatusById(Long userId, UserStatus status, BaseActionRequest request);
    ResponseEntity<ResponseObject> getAccountLockouts(int page, int size, boolean activeOnly);
    ResponseEntity<ResponseObject> getLoginAttemptsByUserId(Long userId, int page, int size, int hoursBack);
    ResponseEntity<ResponseObject> getLockoutStatusByUserId(Long userId);
    
    /**
     * Legacy methods (for backward compatibility) - Will be deprecated
     */
    @Deprecated
    ResponseEntity<ResponseObject> unlockAccount(String username);
    @Deprecated 
    ResponseEntity<ResponseObject> lockAccount(String username, BaseActionRequest request);
    @Deprecated
    ResponseEntity<ResponseObject> changeUserStatus(String username, UserStatus status, BaseActionRequest request);
    @Deprecated
    ResponseEntity<ResponseObject> getLoginAttempts(String username, int page, int size, int hoursBack);
    @Deprecated
    ResponseEntity<ResponseObject> getLockoutStatus(String username);
}
