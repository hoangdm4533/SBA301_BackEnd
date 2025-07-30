package com.example.demologin.serviceImpl;

import com.example.demologin.entity.AccountLockout;
import com.example.demologin.entity.LoginAttempt;
import com.example.demologin.repository.AccountLockoutRepository;
import com.example.demologin.repository.LoginAttemptRepository;
import com.example.demologin.service.BruteForceProtectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BruteForceProtectionServiceImpl implements BruteForceProtectionService {

    private final LoginAttemptRepository loginAttemptRepository;
    private final AccountLockoutRepository accountLockoutRepository;

    // Configuration constants
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int FAILED_ATTEMPTS_WINDOW_MINUTES = 10;
    private static final int LOCKOUT_DURATION_MINUTES = 15;

    @Override
    @Transactional
    public void recordLoginAttempt(String username, String ipAddress, boolean success, String failureReason) {
        LoginAttempt attempt = LoginAttempt.builder()
                .username(username)
                .ipAddress(ipAddress)
                .success(success)
                .attemptTime(LocalDateTime.now())
                .failureReason(failureReason)
                .build();

        loginAttemptRepository.save(attempt);
        log.info("Recorded login attempt for username: {}, success: {}, IP: {}", username, success, ipAddress);
    }

    @Override
    public boolean isAccountLocked(String username) {
        Optional<AccountLockout> lockout = accountLockoutRepository.findActiveAccountLockout(username, LocalDateTime.now());
        return lockout.isPresent();
    }

    @Override
    public AccountLockout getAccountLockout(String username) {
        return accountLockoutRepository.findActiveAccountLockout(username, LocalDateTime.now()).orElse(null);
    }

    @Override
    @Transactional
    public void unlockAccount(String username) {
        Optional<AccountLockout> lockout = accountLockoutRepository.findActiveAccountLockoutByUsername(username);
        if (lockout.isPresent()) {
            AccountLockout accountLockout = lockout.get();
            accountLockout.setActive(false);
            accountLockoutRepository.save(accountLockout);
            log.info("Account unlocked manually for username: {}", username);
        }
    }

    @Override
    @Transactional
    public void handleFailedLogin(String username, String ipAddress, String failureReason) {
        // Record the failed attempt
        recordLoginAttempt(username, ipAddress, false, failureReason);

        // Check if account is already locked
        if (isAccountLocked(username)) {
            log.info("Account already locked for username: {}", username);
            return;
        }

        // Count failed attempts in the last 10 minutes
        LocalDateTime windowStart = LocalDateTime.now().minus(FAILED_ATTEMPTS_WINDOW_MINUTES, ChronoUnit.MINUTES);
        long failedAttempts = loginAttemptRepository.countFailedAttemptsByUsernameAndAttemptTimeAfter(username, windowStart);

        log.info("Failed attempts for username {} in last {} minutes: {}", username, FAILED_ATTEMPTS_WINDOW_MINUTES, failedAttempts);

        // Lock account if exceeded max attempts
        if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
            lockAccount(username);
        }
    }

    @Override
    @Transactional
    public void handleSuccessfulLogin(String username, String ipAddress) {
        recordLoginAttempt(username, ipAddress, true, null);
        
        // Unlock account if it was locked (successful login should unlock)
        unlockAccount(username);
        
        log.info("Successful login recorded for username: {}", username);
    }

    @Override
    public long getRemainingLockoutMinutes(String username) {
        Optional<AccountLockout> lockout = accountLockoutRepository.findActiveAccountLockout(username, LocalDateTime.now());
        if (lockout.isPresent()) {
            LocalDateTime unlockTime = lockout.get().getUnlockTime();
            LocalDateTime now = LocalDateTime.now();
            if (unlockTime.isAfter(now)) {
                return ChronoUnit.MINUTES.between(now, unlockTime);
            }
        }
        return 0;
    }

    private void lockAccount(String username) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime unlockTime = now.plus(LOCKOUT_DURATION_MINUTES, ChronoUnit.MINUTES);

        AccountLockout lockout = AccountLockout.builder()
                .username(username)
                .lockTime(now)
                .unlockTime(unlockTime)
                .reason("Account locked due to " + MAX_FAILED_ATTEMPTS + " failed login attempts within " + FAILED_ATTEMPTS_WINDOW_MINUTES + " minutes")
                .active(true)
                .build();

        accountLockoutRepository.save(lockout);
        log.warn("Account locked for username: {} until {}", username, unlockTime);
    }
}
