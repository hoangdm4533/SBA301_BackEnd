package com.example.demologin.utils;

import com.example.demologin.entity.User;
import com.example.demologin.service.TokenVersionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Utility class for token version operations
 * Provides convenient methods for token invalidation scenarios
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TokenVersionUtils {
    
    private final TokenVersionService tokenVersionService;
    
    /**
     * Invalidate tokens when user changes password
     */
    public void invalidateTokensOnPasswordChange(User user) {
        log.info("Invalidating tokens due to password change for user: {}", user.getUsername());
        tokenVersionService.incrementTokenVersion(user);
    }
    
    /**
     * Invalidate tokens when user's roles/permissions change
     */
    public void invalidateTokensOnRoleChange(User user) {
        log.info("Invalidating tokens due to role/permission change for user: {}", user.getUsername());
        tokenVersionService.incrementTokenVersion(user);
    }
    
    /**
     * Invalidate tokens when user account is locked
     */
    public void invalidateTokensOnAccountLock(User user) {
        log.info("Invalidating tokens due to account lock for user: {}", user.getUsername());
        tokenVersionService.incrementTokenVersion(user);
    }
    
    /**
     * Invalidate tokens when user account is deactivated
     */
    public void invalidateTokensOnAccountDeactivation(User user) {
        log.info("Invalidating tokens due to account deactivation for user: {}", user.getUsername());
        tokenVersionService.incrementTokenVersion(user);
    }
    
    /**
     * Invalidate tokens on security breach or compromise
     */
    public void invalidateTokensOnSecurityBreach(Long userId) {
        log.warn("Invalidating tokens due to security breach for user ID: {}", userId);
        tokenVersionService.invalidateAllTokens(userId);
    }
    
    /**
     * Invalidate tokens when user explicitly logs out from all devices
     */
    public void invalidateTokensOnLogoutAllDevices(User user) {
        log.info("User {} requested logout from all devices", user.getUsername());
        tokenVersionService.incrementTokenVersion(user);
    }
    
    /**
     * Check if token version is still valid (useful in security filters)
     */
    public boolean isTokenStillValid(String username, int tokenVersion) {
        return tokenVersionService.isTokenVersionValidByUsername(username, tokenVersion);
    }
    
    /**
     * Get current token version for validation
     */
    public int getCurrentTokenVersion(String username) {
        return tokenVersionService.getCurrentTokenVersionByUsername(username);
    }
}
