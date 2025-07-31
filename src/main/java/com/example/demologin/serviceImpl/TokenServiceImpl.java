package com.example.demologin.serviceImpl;

import com.example.demologin.entity.User;
import com.example.demologin.repository.UserRepository;
import com.example.demologin.service.TokenService;
import com.example.demologin.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of TokenService that handles JWT token business logic
 * while delegating JWT operations to JwtUtil.
 */
@Service
@Slf4j
public class TokenServiceImpl implements TokenService {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public String generateTokenForUser(User user) {
        log.debug("Generating token for user: {}", user.getUsername());
        return jwtUtil.generateToken(user);
    }

    @Override
    public User getUserByToken(String token) {
        try {
            String username = jwtUtil.extractUsername(token);
            if (username == null) {
                log.warn("Username not found in token");
                return null;
            }
            
            return userRepository.findByUsername(username).orElse(null);
        } catch (Exception e) {
            log.warn("Failed to extract user from token: {}", e.getMessage());
            return null;
        }
    }
}
