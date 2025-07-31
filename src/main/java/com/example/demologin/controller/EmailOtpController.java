package com.example.demologin.controller;

import com.example.demologin.annotation.UserActivity;
import com.example.demologin.dto.request.emailOTP.EmailRequest;
import com.example.demologin.dto.request.emailOTP.OtpRequest;
import com.example.demologin.dto.request.emailOTP.ResetPasswordRequestWithOtp;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.enums.ActivityType;
import com.example.demologin.service.EmailOtpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@Tag(name = "Email OTP", description = "APIs for email verification and password reset using OTP")
    public class EmailOtpController {
    @Autowired
    private EmailOtpService emailOtpService;

    @PostMapping("/send-verification")
    @UserActivity(activityType = ActivityType.EMAIL_VERIFICATION, details = "Email verification OTP sent")
    @Operation(summary = "Send email verification OTP", 
               description = "Send OTP to email for email verification during registration")
    public ResponseEntity<ResponseObject> sendVerificationOtp(@RequestBody @Valid EmailRequest request) {
        return ResponseEntity.ok(emailOtpService.sendVerificationOtp(request));
    }

    @PostMapping("/verify")
    @UserActivity(activityType = ActivityType.OTP_VERIFICATION, details = "Email OTP verification attempt")
    @Operation(summary = "Verify email OTP", 
               description = "Verify the OTP code sent to email")
    public ResponseEntity<ResponseObject> verifyEmailOtp(@RequestBody @Valid OtpRequest request) {
        return ResponseEntity.ok(emailOtpService.verifyEmailOtp(request));
    }

    @PostMapping("/forgot-password")
    @UserActivity(activityType = ActivityType.EMAIL_VERIFICATION, details = "Forgot password OTP sent")
    @Operation(summary = "Send forgot password OTP", 
               description = "Send OTP to email for password reset")
    public ResponseEntity<ResponseObject> sendForgotPasswordOtp(@RequestBody @Valid EmailRequest request) {
        return ResponseEntity.ok(emailOtpService.sendForgotPasswordOtp(request));
    }

    @PostMapping("/reset-password")
    @UserActivity(activityType = ActivityType.PASSWORD_CHANGE, details = "Password reset with OTP")
    @Operation(summary = "Reset password with OTP", 
               description = "Reset user password using verified OTP")
    public ResponseEntity<ResponseObject> resetPasswordWithOtp(@RequestBody @Valid ResetPasswordRequestWithOtp request) {
        return ResponseEntity.ok(emailOtpService.resetPasswordWithOtp(request));
    }

    @PostMapping("/resend")
    @UserActivity(activityType = ActivityType.EMAIL_VERIFICATION, details = "OTP resend request")
    @Operation(summary = "Resend OTP", 
               description = "Resend OTP to the same email address")
    public ResponseEntity<ResponseObject> resendOtp(@RequestBody @Valid EmailRequest request) {
        return ResponseEntity.ok(emailOtpService.resendOtp(request));
    }
}
