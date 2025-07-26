package com.example.demologin.service;

import com.example.demologin.dto.request.emailOTP.EmailRequest;
import com.example.demologin.dto.request.emailOTP.OtpRequest;
import com.example.demologin.dto.request.emailOTP.ResetPasswordRequestWithOtp;
import com.example.demologin.dto.response.ResponseObject;

public interface EmailOtpService {
    ResponseObject sendVerificationOtp(EmailRequest request);
    ResponseObject verifyEmailOtp(OtpRequest request);
    ResponseObject sendForgotPasswordOtp(EmailRequest request);
    ResponseObject resetPasswordWithOtp(ResetPasswordRequestWithOtp request);
    ResponseObject resendOtp(EmailRequest request);
} 