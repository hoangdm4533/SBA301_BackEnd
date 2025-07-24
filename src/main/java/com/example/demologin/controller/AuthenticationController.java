package com.example.demologin.controller;

import com.example.demologin.dto.request.*;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.dto.response.TokenRefreshResponse;
import com.example.demologin.dto.response.UserResponse;
import com.example.demologin.entity.RefreshToken;
import com.example.demologin.entity.User;
import com.example.demologin.exception.exceptions.TokenRefreshException;
import com.example.demologin.mapper.UserMapper;
import com.example.demologin.repository.RefreshTokenRepository;
import com.example.demologin.repository.UserRepository;
import com.example.demologin.service.AuthenticationService;
import com.example.demologin.service.EmailService;
import com.example.demologin.service.RefreshTokenService;
import com.example.demologin.service.TokenService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api")
public class AuthenticationController {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    TokenService tokenService;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    UserMapper userMapper;



    @PostMapping("/register")
    public ResponseEntity<ResponseObject> register(@Valid @RequestBody UserRegistrationRequest request) {
        try {
            User user = authenticationService.register(request);
            return ResponseEntity.ok().body(
                    new ResponseObject(
                            HttpStatus.OK.value(),
                            "Registration successful",
                            userMapper.toUserResponse(user) // Sử dụng mapper để trả về dạng response
                    )
            );
        } catch (RuntimeException e) {
            HttpStatus status = HttpStatus.BAD_REQUEST;

            // Xác định status code cụ thể cho các lỗi thường gặp
            if (e.getMessage().contains("already exists")) {
                status = HttpStatus.CONFLICT;
            }

            return ResponseEntity.status(status).body(
                    new ResponseObject(
                            status.value(),
                            e.getMessage(),
                            null
                    )
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseObject> login(@RequestBody LoginRequest loginRequest) {
        try {
            UserResponse userResponse = authenticationService.login(loginRequest);
            return ResponseEntity.ok()
                    .body(new ResponseObject(HttpStatus.OK.value(), "Login successful", userResponse));
        } catch (RuntimeException e) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            String message = e.getMessage();
            if (message.equals("Account has been locked!")) {
                status = HttpStatus.FORBIDDEN;
            }
            return ResponseEntity.status(status)
                    .body(new ResponseObject(status.value(), message, null));
        }
    }

    @SecurityRequirement(name = "api")
    @PostMapping("/refresh-token")
    public ResponseEntity<ResponseObject> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        try {
            String requestRefreshToken = request.getRefreshToken();
            return refreshTokenService.findByToken(requestRefreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        String token = tokenService.generateToken(user);
                        return ResponseEntity.ok()
                                .body(new ResponseObject(HttpStatus.OK.value(), "Token refreshed successfully",
                                        new TokenRefreshResponse(token, requestRefreshToken)));
                    })
                    .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));
        } catch (TokenRefreshException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseObject(HttpStatus.FORBIDDEN.value(), e.getMessage(), null));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseObject> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("Email không tồn tại"));

            String token = UUID.randomUUID().toString();
            authenticationService.createPasswordResetTokenForAccount(user, token);

            String emailSubject = "Reset Password Token";
            String emailText = "Your reset password token is: " + token;
            emailService.sendEmail(request.getEmail(), emailSubject, emailText);

            return ResponseEntity.ok()
                    .body(new ResponseObject(HttpStatus.OK.value(), "Reset token has been sent to your email.", null));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseObject> resetPassword(
            @RequestParam("token") String token,
            @RequestBody ResetPasswordRequest request
    ) {
        try {
            User user = authenticationService.validatePasswordResetToken(token);
            authenticationService.changePassword(user, request.getNewPassword());
            authenticationService.deleteResetToken(token);
            return ResponseEntity.ok()
                    .body(new ResponseObject(HttpStatus.OK.value(), "Đặt lại mật khẩu thành công", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        }
    }

    @SecurityRequirement(name = "api")
    @PostMapping("/logout")
    @Transactional
    public ResponseEntity<ResponseObject> logout() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();

            user.incrementTokenVersion();
            userRepository.save(user);

            refreshTokenRepository.deleteByUser(user);

            return ResponseEntity.ok()
                    .body(new ResponseObject(HttpStatus.OK.value(), "Logout successful", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Logout failed: " + e.getMessage(), null));
        }
    }


    @PostMapping("/google-login")
    public ResponseEntity<ResponseObject> loginWithGoogle(@RequestBody GoogleLoginRequest request) {
        try {
            // Validate request
            if (request == null) {
                return ResponseEntity.badRequest()
                        .body(new ResponseObject(HttpStatus.BAD_REQUEST.value(), "Request body cannot be null", null));
            }

            if (request.getIdToken() == null || request.getIdToken().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ResponseObject(HttpStatus.BAD_REQUEST.value(), "Google ID token cannot be null or empty", null));
            }

            UserResponse userResponse = authenticationService.authenticateWithGoogle(request);
            return ResponseEntity.ok()
                    .body(new ResponseObject(HttpStatus.OK.value(), "Google login successful", userResponse));
        } catch (IllegalArgumentException e) {
            // Specific handling for token format issues
            return ResponseEntity.badRequest()
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.value(), "Invalid token format: " + e.getMessage(), null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseObject(HttpStatus.UNAUTHORIZED.value(), "Google login failed: " + e.getMessage(), null));
        }
    }

    @SecurityRequirement(name = "api")
    @GetMapping("/oauth2/success")
    public ResponseEntity<ResponseObject> oauth2LoginSuccess(Authentication authentication) {
        try {
            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                OAuth2User oAuth2User = oauthToken.getPrincipal();

                // Extract user information from OAuth2User
                String email = oAuth2User.getAttribute("email");
                String name = oAuth2User.getAttribute("name");

                if (email == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ResponseObject(HttpStatus.BAD_REQUEST.value(), "Email not provided by OAuth2 provider", null));
                }

                // Authenticate with OAuth2
                UserResponse userResponse = authenticationService.authenticateWithOAuth2(email, name != null ? name : email);

                return ResponseEntity.ok()
                        .body(new ResponseObject(HttpStatus.OK.value(), "OAuth2 login successful", userResponse));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponseObject(HttpStatus.UNAUTHORIZED.value(), "Not an OAuth2 authentication", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.value(), "OAuth2 login failed: " + e.getMessage(), null));
        }
    }

    @SecurityRequirement(name = "api")
    @GetMapping("/oauth2/failure")
    public ResponseEntity<ResponseObject> oauth2LoginFailure() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ResponseObject(HttpStatus.UNAUTHORIZED.value(), "OAuth2 login failed", null));
    }

    @PostMapping("/facebook-login")
    public ResponseEntity<ResponseObject> loginWithFacebook(@RequestBody FacebookLoginRequest request) {
        try {
            // Validate request
            if (request == null) {
                return ResponseEntity.badRequest()
                        .body(new ResponseObject(HttpStatus.BAD_REQUEST.value(), "Request body cannot be null", null));
            }

            if (request.getAccessToken() == null || request.getAccessToken().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ResponseObject(HttpStatus.BAD_REQUEST.value(), "Facebook access token cannot be null or empty", null));
            }

            UserResponse userResponse = authenticationService.authenticateWithFacebook(request);
            return ResponseEntity.ok()
                    .body(new ResponseObject(HttpStatus.OK.value(), "Facebook login successful", userResponse));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.value(), "Invalid token format: " + e.getMessage(), null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseObject(HttpStatus.UNAUTHORIZED.value(), "Facebook login failed: " + e.getMessage(), null));
        }
    }
}
