package com.example.demologin.controller;

import com.example.demologin.annotation.SecuredEndpoint;
import com.example.demologin.annotation.UserActivity;
import com.example.demologin.dto.request.*;
import com.example.demologin.dto.response.LoginResponse;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.dto.response.TokenRefreshResponse;
import com.example.demologin.dto.response.UserResponse;
import com.example.demologin.entity.User;
import com.example.demologin.enums.ActivityType;
import com.example.demologin.mapper.UserMapper;
import com.example.demologin.service.AuthenticationService;
import com.example.demologin.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
public class AuthenticationController {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    UserMapper userMapper;



    @PostMapping("/register")
    @UserActivity(activityType = ActivityType.REGISTRATION, details = "User registration")
    public ResponseEntity<ResponseObject> register(@Valid @RequestBody UserRegistrationRequest request) {
        return authenticationService.register(request);
    }

    @PostMapping("/login")
    @UserActivity(activityType = ActivityType.LOGIN_ATTEMPT, details = "User login attempt")
    public ResponseEntity<ResponseObject> login(@RequestBody @Valid LoginRequest loginRequest) {
        return authenticationService.login(loginRequest);
    }

    @SecuredEndpoint("USER_TOKEN_MANAGEMENT")
    @PostMapping("/refresh-token")
    @UserActivity(activityType = ActivityType.TOKEN_REFRESH, details = "Token refresh request")
    public ResponseEntity<ResponseObject> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        TokenRefreshResponse tokenRefreshResponse = refreshTokenService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Token refreshed successfully", tokenRefreshResponse));
    }

    @PostMapping("/google-login")
    @UserActivity(activityType = ActivityType.LOGIN_ATTEMPT, details = "Google OAuth login attempt")
    public ResponseEntity<ResponseObject> loginWithGoogle(@RequestBody GoogleLoginRequest request) {
        UserResponse userResponse = authenticationService.authenticateWithGoogle(request);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Google login successful", userResponse));
    }

    @SecuredEndpoint("USER_TOKEN_MANAGEMENT")
    @GetMapping("/oauth2/success")
    @UserActivity(activityType = ActivityType.LOGIN_SUCCESS, details = "OAuth2 login successful")
    public ResponseEntity<ResponseObject> oauth2LoginSuccess(Authentication authentication) {
        UserResponse userResponse = authenticationService.authenticateWithOAuth2FromAuthentication(authentication);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "OAuth2 login successful", userResponse));
    }

    @SecuredEndpoint("USER_TOKEN_MANAGEMENT")
    @GetMapping("/oauth2/failure")
    @UserActivity(activityType = ActivityType.LOGIN_FAILED, details = "OAuth2 login failed")
    public ResponseEntity<ResponseObject> oauth2LoginFailure() {
        authenticationService.handleOAuth2Failure();
        return null; // This will never be reached due to exception
    }

    @PostMapping("/facebook-login")
    @UserActivity(activityType = ActivityType.LOGIN_ATTEMPT, details = "Facebook OAuth login attempt")
    public ResponseEntity<ResponseObject> loginWithFacebook(@RequestBody FacebookLoginRequest request) {
        UserResponse userResponse = authenticationService.authenticateWithFacebook(request);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Facebook login successful", userResponse));
    }
}
