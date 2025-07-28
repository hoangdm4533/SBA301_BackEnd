package com.example.demologin.service;

import com.example.demologin.dto.request.FacebookLoginRequest;
import com.example.demologin.dto.request.GoogleLoginRequest;
import com.example.demologin.dto.request.LoginRequest;
import com.example.demologin.dto.request.UserRegistrationRequest;
import com.example.demologin.dto.response.UserResponse;
import com.example.demologin.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthenticationService extends UserDetailsService {
    User register(UserRegistrationRequest request);
    UserResponse login(LoginRequest loginRequest);
    void logout();
    void createPasswordResetTokenForAccount(User user, String token);
    User validatePasswordResetToken(String token);
    void changePassword(User user, String newPassword);
    void deleteResetToken(String token);
    UserResponse authenticateWithGoogle(GoogleLoginRequest request);
    UserResponse authenticateWithOAuth2(String email, String name);
    UserResponse authenticateWithOAuth2FromAuthentication(org.springframework.security.core.Authentication authentication);
    UserResponse authenticateWithFacebook(FacebookLoginRequest request);
    UserResponse getUserResponse(String email, String name);
    void handleOAuth2Failure();
}