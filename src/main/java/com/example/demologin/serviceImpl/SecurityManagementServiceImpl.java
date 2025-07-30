package com.example.demologin.serviceImpl;

import com.example.demologin.dto.request.BaseActionRequest;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.entity.AccountLockout;
import com.example.demologin.entity.LoginAttempt;
import com.example.demologin.entity.User;
import com.example.demologin.enums.UserStatus;
import com.example.demologin.exception.exceptions.NotFoundException;
import com.example.demologin.repository.AccountLockoutRepository;
import com.example.demologin.repository.LoginAttemptRepository;
import com.example.demologin.repository.UserRepository;
import com.example.demologin.service.BruteForceProtectionService;
import com.example.demologin.service.SecurityManagementService;
import com.example.demologin.utils.AccountUtils;
import com.example.demologin.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityManagementServiceImpl implements SecurityManagementService {
    
    private final BruteForceProtectionService bruteForceProtectionService;
    private final AccountLockoutRepository accountLockoutRepository;
    private final LoginAttemptRepository loginAttemptRepository;
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public ResponseObject unlockAccount(String username) {
        // Validate input parameters
        if (username == null || username.trim().isEmpty()) {
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(), "Username cannot be null or empty", null);
        }
        
        String trimmedUsername = username.trim();
        User adminUser = AccountUtils.getCurrentUser();
        
        if (adminUser == null) {
            return new ResponseObject(HttpStatus.UNAUTHORIZED.value(), "Admin user not found", null);
        }
        
        try {
            if (bruteForceProtectionService.isAccountLocked(trimmedUsername)) {
                bruteForceProtectionService.unlockAccount(trimmedUsername);
                log.info("Admin {} unlocked account for user {}", adminUser.getUsername(), trimmedUsername);
                
                Map<String, Object> data = Map.of(
                    "username", trimmedUsername,
                    "unlockedAt", LocalDateTime.now(),
                    "unlockedBy", adminUser.getUsername()
                );
                
                return new ResponseObject(HttpStatus.OK.value(), "Account unlocked successfully", data);
            } else {
                Map<String, Object> data = Map.of(
                    "username", trimmedUsername,
                    "checkedAt", LocalDateTime.now(),
                    "checkedBy", adminUser.getUsername()
                );
                
                return new ResponseObject(HttpStatus.OK.value(), "Account is not locked", data);
            }
        } catch (Exception e) {
            log.error("Error unlocking account for user {}: {}", trimmedUsername, e.getMessage());
            return new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error unlocking account: " + e.getMessage(), null);
        }
    }
    
    @Override
    @Transactional
    public ResponseObject lockAccount(String username, BaseActionRequest request) {
        // Validate input parameters
        if (username == null || username.trim().isEmpty()) {
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(), "Username cannot be null or empty", null);
        }
        if (request == null || request.getReason() == null || request.getReason().trim().isEmpty()) {
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(), "Lock reason cannot be null or empty", null);
        }
        
        String trimmedUsername = username.trim();
        
        try {
            // Verify user exists
            userRepository.findByUsername(trimmedUsername)
                .orElseThrow(() -> new NotFoundException("User not found with username: " + trimmedUsername));
            
            User adminUser = AccountUtils.getCurrentUser();
            if (adminUser == null) {
                return new ResponseObject(HttpStatus.UNAUTHORIZED.value(), "Admin user not found", null);
            }
            
            // Create manual lockout
            AccountLockout lockout = new AccountLockout();
            lockout.setUsername(trimmedUsername);
            lockout.setReason("ADMIN_MANUAL_LOCK: " + request.getReason().trim());
            lockout.setLockTime(LocalDateTime.now());
            lockout.setUnlockTime(LocalDateTime.now().plusYears(10)); // Long-term lock for manual locks
            accountLockoutRepository.save(lockout);
            
            log.info("Admin {} manually locked account for user {}. Reason: {}", 
                    adminUser.getUsername(), trimmedUsername, request.getReason());
            
            Map<String, Object> data = Map.of(
                "username", trimmedUsername,
                "lockedAt", LocalDateTime.now(),
                "lockedBy", adminUser.getUsername(),
                "reason", request.getReason().trim(),
                "lockType", "MANUAL_ADMIN_LOCK"
            );
            
            return new ResponseObject(HttpStatus.OK.value(), "Account locked successfully", data);
        } catch (NotFoundException e) {
            return new ResponseObject(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (Exception e) {
            log.error("Error locking account for user {}: {}", trimmedUsername, e.getMessage());
            return new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error locking account: " + e.getMessage(), null);
        }
    }
    
    @Override
    @Transactional
    public ResponseObject changeUserStatus(String username, UserStatus status, BaseActionRequest request) {
        // Validate input parameters
        if (username == null || username.trim().isEmpty()) {
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(), "Username cannot be null or empty", null);
        }
        if (status == null) {
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(), "User status cannot be null", null);
        }
        if (request == null || request.getReason() == null || request.getReason().trim().isEmpty()) {
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(), "Change reason cannot be null or empty", null);
        }
        
        String trimmedUsername = username.trim();
        
        try {
            User targetUser = userRepository.findByUsername(trimmedUsername)
                .orElseThrow(() -> new NotFoundException("User not found with username: " + trimmedUsername));
            
            User adminUser = AccountUtils.getCurrentUser();
            if (adminUser == null) {
                return new ResponseObject(HttpStatus.UNAUTHORIZED.value(), "Admin user not found", null);
            }
            
            UserStatus oldStatus = targetUser.getStatus();
            if (oldStatus == null) {
                oldStatus = UserStatus.ACTIVE; // Default fallback
            }
            
            targetUser.setStatus(status);
            userRepository.save(targetUser);
            
            log.info("Admin {} changed user {} status from {} to {}. Reason: {}", 
                    adminUser.getUsername(), trimmedUsername, oldStatus, status, request.getReason());
            
            Map<String, Object> data = Map.of(
                "username", trimmedUsername,
                "oldStatus", oldStatus.toString(),
                "newStatus", status.toString(),
                "changedAt", LocalDateTime.now(),
                "changedBy", adminUser.getUsername(),
                "reason", request.getReason().trim()
            );
            
            return new ResponseObject(HttpStatus.OK.value(), "User status changed successfully", data);
        } catch (NotFoundException e) {
            return new ResponseObject(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (Exception e) {
            log.error("Error changing status for user {}: {}", trimmedUsername, e.getMessage());
            return new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error changing user status: " + e.getMessage(), null);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public ResponseObject getAccountLockouts(int page, int size, boolean activeOnly) {
        // Validate input parameters
        if (page < 0) {
            page = 0;
        }
        if (size <= 0 || size > 100) {
            size = 20;
        }
        
        Pageable pageable = PageUtils.createPageable(page, size);
        Page<AccountLockout> lockouts = accountLockoutRepository.findAll(pageable);
        
        if (lockouts == null || lockouts.isEmpty()) {
            // Return empty but valid page structure
            Map<String, Object> emptyData = Map.of(
                "content", List.of(),
                "pageNumber", page,
                "pageSize", size,
                "totalElements", 0L,
                "totalPages", 0,
                "last", true,
                "activeOnly", activeOnly
            );
            return new ResponseObject(HttpStatus.OK.value(), "No account lockouts found", emptyData);
        }
        
        List<AccountLockout> content = lockouts.getContent();
        if (content == null) {
            content = List.of();
        }
        
        Map<String, Object> data = Map.of(
            "content", content,
            "pageNumber", lockouts.getNumber(),
            "pageSize", lockouts.getSize(),
            "totalElements", lockouts.getTotalElements(),
            "totalPages", lockouts.getTotalPages(),
            "last", lockouts.isLast(),
            "activeOnly", activeOnly
        );
        
        return new ResponseObject(HttpStatus.OK.value(), "Account lockouts retrieved successfully", data);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ResponseObject getLoginAttempts(String username, int page, int size, int hoursBack) {
        // Validate input parameters
        if (username == null || username.trim().isEmpty()) {
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(), "Username cannot be null or empty", null);
        }
        if (page < 0) {
            page = 0;
        }
        if (size <= 0 || size > 100) {
            size = 20;
        }
        if (hoursBack <= 0 || hoursBack > 168) { // Max 7 days
            hoursBack = 24;
        }
        
        LocalDateTime since = LocalDateTime.now().minusHours(hoursBack);
        List<LoginAttempt> attempts = loginAttemptRepository.findByUsernameAndAttemptTimeAfter(username.trim(), since);
        
        if (attempts == null) {
            attempts = List.of();
        }
        
        Map<String, Object> data = Map.of(
            "username", username.trim(),
            "hoursBack", hoursBack,
            "totalAttempts", attempts.size(),
            "attempts", attempts,
            "since", since,
            "queriedAt", LocalDateTime.now()
        );
        
        return new ResponseObject(HttpStatus.OK.value(), "Login attempts retrieved successfully", data);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ResponseObject getLockoutStatus(String username) {
        // Validate input parameters
        if (username == null || username.trim().isEmpty()) {
            return new ResponseObject(HttpStatus.BAD_REQUEST.value(), "Username cannot be null or empty", null);
        }
        
        String trimmedUsername = username.trim();
        boolean isLocked = bruteForceProtectionService.isAccountLocked(trimmedUsername);
        
        Map<String, Object> data = Map.of(
            "username", trimmedUsername,
            "isLocked", isLocked,
            "checkedAt", LocalDateTime.now()
        );
        
        if (isLocked) {
            try {
                long remainingMinutes = bruteForceProtectionService.getRemainingLockoutMinutes(trimmedUsername);
                AccountLockout lockout = bruteForceProtectionService.getAccountLockout(trimmedUsername);
                
                data = Map.of(
                    "username", trimmedUsername,
                    "isLocked", true,
                    "remainingMinutes", remainingMinutes,
                    "lockReason", lockout != null ? lockout.getReason() : "Unknown",
                    "lockTime", lockout != null ? lockout.getLockTime() : null,
                    "unlockTime", lockout != null ? lockout.getUnlockTime() : null,
                    "checkedAt", LocalDateTime.now()
                );
            } catch (Exception e) {
                log.error("Error getting lockout details for user {}: {}", trimmedUsername, e.getMessage());
                data = Map.of(
                    "username", trimmedUsername,
                    "isLocked", true,
                    "error", "Unable to retrieve lockout details",
                    "checkedAt", LocalDateTime.now()
                );
            }
        }
        
        return new ResponseObject(HttpStatus.OK.value(), "Lockout status retrieved successfully", data);
    }
}
