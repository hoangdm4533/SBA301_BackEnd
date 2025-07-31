package com.example.demologin.controller;

import com.example.demologin.annotation.SecuredEndpoint;
import com.example.demologin.annotation.UserActivity;
import com.example.demologin.dto.request.user.UserRegistrationRequest;
import com.example.demologin.dto.request.login.LoginRequest;
import com.example.demologin.dto.request.login.GoogleLoginRequest;
import com.example.demologin.dto.request.login.FacebookLoginRequest;
import com.example.demologin.dto.request.token.TokenRefreshRequest;
import com.example.demologin.dto.response.LoginResponse;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.dto.response.TokenRefreshResponse;
import com.example.demologin.dto.response.UserResponse;
import com.example.demologin.enums.ActivityType;
import com.example.demologin.service.AuthenticationService;
import com.example.demologin.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
@Tag(name = "Authentication", description = "APIs for user authentication, registration, and OAuth login")
    public class AuthenticationController {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    @UserActivity(activityType = ActivityType.REGISTRATION, details = "User registration")
    @Operation(summary = "User registration", 
               description = "Register a new user account with email verification")
    public ResponseEntity<ResponseObject> register(@Valid @RequestBody UserRegistrationRequest request) {
        return authenticationService.register(request);
    }

    @PostMapping("/login")
    @UserActivity(activityType = ActivityType.LOGIN_ATTEMPT, details = "User login attempt")
    @Operation(summary = "User login", 
               description = "Authenticate user with email and password")
    public ResponseEntity<ResponseObject> login(@RequestBody @Valid LoginRequest loginRequest) {
        return authenticationService.login(loginRequest);
    }

    @SecuredEndpoint("USER_TOKEN_MANAGEMENT")
    @PostMapping("/refresh-token")
    @UserActivity(activityType = ActivityType.TOKEN_REFRESH, details = "Token refresh request")
    @Operation(summary = "Refresh access token", 
               description = "Get new access token using refresh token")
    public ResponseEntity<ResponseObject> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        TokenRefreshResponse tokenRefreshResponse = refreshTokenService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(new ResponseObject(200, "Token refreshed successfully", tokenRefreshResponse));
    }

    @PostMapping("/google-login")
    @UserActivity(activityType = ActivityType.LOGIN_ATTEMPT, details = "Google OAuth login attempt")
    @Operation(summary = "Google OAuth login", 
               description = "Authenticate user with Google OAuth token")
    public ResponseEntity<ResponseObject> loginWithGoogle(@RequestBody GoogleLoginRequest request) {
        UserResponse userResponse = authenticationService.authenticateWithGoogle(request);
        return ResponseEntity.ok(new ResponseObject(200, "Google login successful", userResponse));
    }

    @SecuredEndpoint("USER_TOKEN_MANAGEMENT")
    @GetMapping("/oauth2/success")
    @UserActivity(activityType = ActivityType.LOGIN_SUCCESS, details = "OAuth2 login successful")
    @Operation(summary = "OAuth2 login success callback", 
               description = "Handle successful OAuth2 authentication callback")
    public ResponseEntity<ResponseObject> oauth2LoginSuccess(Authentication authentication) {
        UserResponse userResponse = authenticationService.authenticateWithOAuth2FromAuthentication(authentication);
        return ResponseEntity.ok(new ResponseObject(200, "OAuth2 login successful", userResponse));
    }

    @SecuredEndpoint("USER_TOKEN_MANAGEMENT")
    @GetMapping("/oauth2/failure")
    @UserActivity(activityType = ActivityType.LOGIN_FAILED, details = "OAuth2 login failed")
    @Operation(summary = "OAuth2 login failure callback", 
               description = "Handle failed OAuth2 authentication callback")
    public ResponseEntity<ResponseObject> oauth2LoginFailure() {
        authenticationService.handleOAuth2Failure();
        return null; // This will never be reached due to exception
    }

    @PostMapping("/facebook-login")
    @UserActivity(activityType = ActivityType.LOGIN_ATTEMPT, details = "Facebook OAuth login attempt")
    @Operation(summary = "Facebook OAuth login", 
               description = "Authenticate user with Facebook OAuth token")
    public ResponseEntity<ResponseObject> loginWithFacebook(@RequestBody FacebookLoginRequest request) {
        UserResponse userResponse = authenticationService.authenticateWithFacebook(request);
        return ResponseEntity.ok(new ResponseObject(200, "Facebook login successful", userResponse));
    }
}
