package com.example.demologin.dto.request.emailOTP;

import lombok.Data;

@Data
public class ResetPasswordRequestWithOtp extends ResetPasswordRequest {
    private String email;
    private String otp;
} 
