package com.example.demologin.service;

import com.example.demologin.dto.request.login.FacebookLoginRequest;
import com.example.demologin.dto.request.login.GoogleLoginRequest;
import com.example.demologin.dto.request.login.LoginRequest;
import com.example.demologin.dto.request.user.UserRegistrationRequest;
import com.example.demologin.dto.response.LoginResponse;
import com.example.demologin.dto.response.UserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthenticationService extends UserDetailsService {
    UserResponse register(UserRegistrationRequest request);
    LoginResponse login(LoginRequest loginRequest);
    UserResponse authenticateWithGoogle(GoogleLoginRequest request);
    UserResponse authenticateWithOAuth2FromAuthentication(org.springframework.security.core.Authentication authentication);
    UserResponse authenticateWithFacebook(FacebookLoginRequest request);
    UserResponse getUserResponse(String email, String name);
    void handleOAuth2Failure();
}
