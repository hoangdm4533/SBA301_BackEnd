package com.example.demologin.controller;

import com.example.demologin.dto.request.*;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.dto.response.TokenRefreshResponse;
import com.example.demologin.dto.response.UserResponse;
import com.example.demologin.entity.User;
import com.example.demologin.mapper.UserMapper;
import com.example.demologin.service.AuthenticationService;
import com.example.demologin.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
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
    public ResponseEntity<ResponseObject> register(@Valid @RequestBody UserRegistrationRequest request) {
        User user = authenticationService.register(request);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Registration successful", userMapper.toUserResponse(user)));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseObject> login(@RequestBody @Valid LoginRequest loginRequest) {
        UserResponse userResponse = authenticationService.login(loginRequest);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Login successful", userResponse));
    }

    @SecurityRequirement(name = "api")
    @PostMapping("/refresh-token")
    public ResponseEntity<ResponseObject> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        TokenRefreshResponse tokenRefreshResponse = refreshTokenService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Token refreshed successfully", tokenRefreshResponse));
    }

    @SecurityRequirement(name = "api")
    @PostMapping("/logout")
    @Transactional
    public ResponseEntity<ResponseObject> logout() {
        authenticationService.logout();
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Logout successful", null));
    }


    @PostMapping("/google-login")
    public ResponseEntity<ResponseObject> loginWithGoogle(@RequestBody GoogleLoginRequest request) {
        UserResponse userResponse = authenticationService.authenticateWithGoogle(request);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Google login successful", userResponse));
    }

    @SecurityRequirement(name = "api")
    @GetMapping("/oauth2/success")
    public ResponseEntity<ResponseObject> oauth2LoginSuccess(Authentication authentication) {
        UserResponse userResponse = authenticationService.authenticateWithOAuth2FromAuthentication(authentication);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "OAuth2 login successful", userResponse));
    }

    @SecurityRequirement(name = "api")
    @GetMapping("/oauth2/failure")
    public ResponseEntity<ResponseObject> oauth2LoginFailure() {
        authenticationService.handleOAuth2Failure();
        return null; // This will never be reached due to exception
    }

    @PostMapping("/facebook-login")
    public ResponseEntity<ResponseObject> loginWithFacebook(@RequestBody FacebookLoginRequest request) {
        UserResponse userResponse = authenticationService.authenticateWithFacebook(request);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Facebook login successful", userResponse));
    }
}
