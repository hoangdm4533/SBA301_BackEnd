package com.example.demologin.serviceImpl;

import com.example.demologin.entity.User;
import com.example.demologin.exception.exceptions.NotFoundException;
import com.example.demologin.repository.UserRepository;
import com.example.demologin.service.SessionManagementService;
import com.example.demologin.service.TokenVersionService;
import com.example.demologin.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionManagementServiceImpl implements SessionManagementService {
    
    private final TokenVersionService tokenVersionService;
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public void logoutCurrentDevice() {
        User currentUser = AccountUtils.getCurrentUser();
        log.info("User {} logging out from current device", currentUser.getUsername());
        
        // Invalidate current token by incrementing token version
        tokenVersionService.incrementTokenVersionByUserId(currentUser.getUserId());
    }

    @Override
    @Transactional
    public void logoutFromAllDevices() {
        User currentUser = AccountUtils.getCurrentUser();
        log.info("User {} logging out from all devices", currentUser.getUsername());
        
        // Invalidate all tokens by incrementing token version
        tokenVersionService.incrementTokenVersionByUserId(currentUser.getUserId());
    }
    
    @Override
    @Transactional
    public void forceLogoutUser(Long userId) {
        User targetUser = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        
        User adminUser = AccountUtils.getCurrentUser();
        log.info("Admin {} force logging out user {} from all devices", 
                adminUser.getUsername(), targetUser.getUsername());
        
        // Invalidate all tokens for target user
        tokenVersionService.incrementTokenVersionByUserId(userId);
    }
}
