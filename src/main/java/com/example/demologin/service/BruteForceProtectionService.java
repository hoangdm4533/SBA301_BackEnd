package com.example.demologin.service;

import com.example.demologin.entity.AccountLockout;

public interface BruteForceProtectionService {
    
    /**
     * Record a login attempt
     */
    void recordLoginAttempt(String username, String ipAddress, boolean success, String failureReason);
    
    /**
     * Check if account is locked due to brute force attempts
     */
    boolean isAccountLocked(String username);
    
    /**
     * Get account lockout details if locked
     */
    AccountLockout getAccountLockout(String username);
    
    /**
     * Unlock account manually (admin function)
     */
    void unlockAccount(String username);
    
    /**
     * Check and handle failed login attempts (lock account if needed)
     */
    void handleFailedLogin(String username, String ipAddress, String failureReason);
    
    /**
     * Handle successful login (reset failed attempts)
     */
    void handleSuccessfulLogin(String username, String ipAddress);
    
    /**
     * Get remaining lockout time in minutes
     */
    long getRemainingLockoutMinutes(String username);
}
