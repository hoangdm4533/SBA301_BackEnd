package com.example.demologin.service;

import com.example.demologin.dto.request.login.FacebookLoginRequest;
import com.example.demologin.dto.request.login.GoogleLoginRequest;
import com.example.demologin.dto.request.login.LoginRequest;
import com.example.demologin.dto.request.user.UserRegistrationRequest;
import com.example.demologin.dto.response.LoginResponse;
import com.example.demologin.dto.response.UserResponse;
import com.example.demologin.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthenticationService extends UserDetailsService {
    UserResponse register(UserRegistrationRequest request);
    LoginResponse login(LoginRequest loginRequest);
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
