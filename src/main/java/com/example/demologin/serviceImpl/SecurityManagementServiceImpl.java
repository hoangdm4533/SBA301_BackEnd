package com.example.demologin.serviceImpl;

import com.example.demologin.dto.request.BaseActionRequest;
import com.example.demologin.dto.response.PageResponse;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.entity.AccountLockout;
import com.example.demologin.entity.LoginAttempt;
import com.example.demologin.entity.User;
import com.example.demologin.enums.UserStatus;
import com.example.demologin.exception.exceptions.NotFoundException;
import com.example.demologin.exception.exceptions.ValidationException;
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
import org.springframework.http.ResponseEntity;
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
    
    // ================ MAIN METHODS WITH USER ID ================
    
    @Override
    @Transactional
    public ResponseEntity<ResponseObject> unlockAccountById(Long userId) {
        if (userId == null) {
            throw new ValidationException("User ID cannot be null");
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        
        User adminUser = AccountUtils.getCurrentUser();
        if (adminUser == null) {
            throw new NotFoundException("Admin user not found");
        }
        
        try {
            if (bruteForceProtectionService.isAccountLocked(user.getUsername())) {
                bruteForceProtectionService.unlockAccount(user.getUsername());
                log.info("Admin {} unlocked account for user ID {} ({})", adminUser.getUsername(), userId, user.getUsername());
                
                Map<String, Object> data = Map.of(
                    "userId", userId,
                    "username", user.getUsername(),
                    "unlockedAt", LocalDateTime.now(),
                    "unlockedBy", adminUser.getUsername()
                );
                
                ResponseObject responseObject = new ResponseObject(HttpStatus.OK.value(), "Account unlocked successfully", data);
                return ResponseEntity.ok(responseObject);
            } else {
                Map<String, Object> data = Map.of(
                    "userId", userId,
                    "username", user.getUsername(),
                    "checkedAt", LocalDateTime.now(),
                    "checkedBy", adminUser.getUsername()
                );
                
                ResponseObject responseObject = new ResponseObject(HttpStatus.OK.value(), "Account is not locked", data);
                return ResponseEntity.ok(responseObject);
            }
        } catch (Exception e) {
            log.error("Error unlocking account for user ID {}: {}", userId, e.getMessage());
            ResponseObject responseObject = new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error unlocking account: " + e.getMessage(), null);
            return ResponseEntity.ok(responseObject);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> lockAccountById(Long userId, BaseActionRequest request) {
        if (userId == null) {
            throw new ValidationException("User ID cannot be null");
        }
        if (request == null || request.getReason() == null || request.getReason().trim().isEmpty()) {
            throw new ValidationException("Lock reason cannot be null or empty");
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        
        User adminUser = AccountUtils.getCurrentUser();
        if (adminUser == null) {
            throw new NotFoundException("Admin user not found");
        }
        
        try {
            // Create manual lockout
            AccountLockout lockout = new AccountLockout();
            lockout.setUsername(user.getUsername());
            lockout.setReason("ADMIN_MANUAL_LOCK: " + request.getReason().trim());
            lockout.setLockTime(LocalDateTime.now());
            lockout.setUnlockTime(LocalDateTime.now().plusYears(10)); // Long-term lock for manual locks
            accountLockoutRepository.save(lockout);
            
            log.info("Admin {} manually locked account for user ID {} ({}). Reason: {}", 
                    adminUser.getUsername(), userId, user.getUsername(), request.getReason());
            
            Map<String, Object> data = Map.of(
                "userId", userId,
                "username", user.getUsername(),
                "lockedAt", LocalDateTime.now(),
                "lockedBy", adminUser.getUsername(),
                "reason", request.getReason().trim(),
                "lockType", "MANUAL_ADMIN_LOCK"
            );
            
            ResponseObject responseObject = new ResponseObject(HttpStatus.OK.value(), "Account locked successfully", data);
            return ResponseEntity.ok(responseObject);
        } catch (Exception e) {
            log.error("Error locking account for user ID {}: {}", userId, e.getMessage());
            ResponseObject responseObject = new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error locking account: " + e.getMessage(), null);
            return ResponseEntity.ok(responseObject);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> changeUserStatusById(Long userId, UserStatus status, BaseActionRequest request) {
        if (userId == null) {
            throw new ValidationException("User ID cannot be null");
        }
        if (status == null) {
            throw new ValidationException("User status cannot be null");
        }
        if (request == null || request.getReason() == null || request.getReason().trim().isEmpty()) {
            throw new ValidationException("Change reason cannot be null or empty");
        }
        
        User targetUser = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        
        User adminUser = AccountUtils.getCurrentUser();
        if (adminUser == null) {
            throw new NotFoundException("Admin user not found");
        }
        
        try {
            UserStatus oldStatus = targetUser.getStatus();
            if (oldStatus == null) {
                oldStatus = UserStatus.ACTIVE; // Default fallback
            }
            
            targetUser.setStatus(status);
            userRepository.save(targetUser);
            
            log.info("Admin {} changed user ID {} ({}) status from {} to {}. Reason: {}", 
                    adminUser.getUsername(), userId, targetUser.getUsername(), oldStatus, status, request.getReason());
            
            Map<String, Object> data = Map.of(
                "userId", userId,
                "username", targetUser.getUsername(),
                "oldStatus", oldStatus.toString(),
                "newStatus", status.toString(),
                "changedAt", LocalDateTime.now(),
                "changedBy", adminUser.getUsername(),
                "reason", request.getReason().trim()
            );
            
            ResponseObject responseObject = new ResponseObject(HttpStatus.OK.value(), "User status changed successfully", data);
            return ResponseEntity.ok(responseObject);
        } catch (Exception e) {
            log.error("Error changing status for user ID {}: {}", userId, e.getMessage());
            ResponseObject responseObject = new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error changing user status: " + e.getMessage(), null);
            return ResponseEntity.ok(responseObject);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseObject> getAccountLockouts(int page, int size, boolean activeOnly) {
        page = PageUtils.normalizePageNumber(page);
        size = PageUtils.normalizePageSize(size);
        
        Pageable pageable = PageUtils.createPageable(page, size);
        Page<AccountLockout> lockouts = accountLockoutRepository.findAll(pageable);
        
        if (lockouts.isEmpty()) {
            throw new NotFoundException("No account lockouts found");
        }
        
        PageResponse<AccountLockout> pageResponse = PageUtils.toPageResponse(lockouts);
        ResponseObject responseObject = new ResponseObject(HttpStatus.OK.value(), "Account lockouts retrieved successfully", pageResponse);
        return ResponseEntity.ok(responseObject);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseObject> getLoginAttemptsByUserId(Long userId, int page, int size, int hoursBack) {
        if (userId == null) {
            throw new ValidationException("User ID cannot be null");
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        
        page = PageUtils.normalizePageNumber(page);
        size = PageUtils.normalizePageSize(size);
        
        if (hoursBack <= 0 || hoursBack > 168) { // Max 7 days
            hoursBack = 24;
        }
        
        LocalDateTime since = LocalDateTime.now().minusHours(hoursBack);
        List<LoginAttempt> attempts = loginAttemptRepository.findByUsernameAndAttemptTimeAfter(user.getUsername(), since);
        
        if (attempts == null) {
            attempts = List.of();
        }
        
        Map<String, Object> data = Map.of(
            "userId", userId,
            "username", user.getUsername(),
            "hoursBack", hoursBack,
            "totalAttempts", attempts.size(),
            "attempts", attempts,
            "since", since,
            "queriedAt", LocalDateTime.now()
        );
        
        ResponseObject responseObject = new ResponseObject(HttpStatus.OK.value(), "Login attempts retrieved successfully", data);
        return ResponseEntity.ok(responseObject);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseObject> getLockoutStatusByUserId(Long userId) {
        if (userId == null) {
            throw new ValidationException("User ID cannot be null");
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        
        boolean isLocked = bruteForceProtectionService.isAccountLocked(user.getUsername());
        
        Map<String, Object> data = Map.of(
            "userId", userId,
            "username", user.getUsername(),
            "isLocked", isLocked,
            "checkedAt", LocalDateTime.now()
        );
        
        if (isLocked) {
            try {
                long remainingMinutes = bruteForceProtectionService.getRemainingLockoutMinutes(user.getUsername());
                AccountLockout lockout = bruteForceProtectionService.getAccountLockout(user.getUsername());
                
                data = Map.of(
                    "userId", userId,
                    "username", user.getUsername(),
                    "isLocked", true,
                    "remainingMinutes", remainingMinutes,
                    "lockReason", lockout != null ? lockout.getReason() : "Unknown",
                    "lockTime", lockout != null ? lockout.getLockTime() : null,
                    "unlockTime", lockout != null ? lockout.getUnlockTime() : null,
                    "checkedAt", LocalDateTime.now()
                );
            } catch (Exception e) {
                log.error("Error getting lockout details for user ID {}: {}", userId, e.getMessage());
                data = Map.of(
                    "userId", userId,
                    "username", user.getUsername(),
                    "isLocked", true,
                    "error", "Unable to retrieve lockout details",
                    "checkedAt", LocalDateTime.now()
                );
            }
        }
        
        ResponseObject responseObject = new ResponseObject(HttpStatus.OK.value(), "Lockout status retrieved successfully", data);
        return ResponseEntity.ok(responseObject);
    }
}
