package com.example.demologin.serviceImpl;

import com.example.demologin.dto.request.emailOTP.EmailRequest;
import com.example.demologin.dto.request.emailOTP.OtpRequest;
import com.example.demologin.dto.request.emailOTP.ResetPasswordRequestWithOtp;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.entity.EmailOtp;
import com.example.demologin.entity.User;
import com.example.demologin.exception.exceptions.BadRequestException;
import com.example.demologin.exception.exceptions.NotFoundException;
import com.example.demologin.repository.EmailOtpRepository;
import com.example.demologin.repository.UserRepository;
import com.example.demologin.service.EmailOtpService;
import com.example.demologin.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@EnableScheduling
@Slf4j
public class EmailOtpServiceImpl implements EmailOtpService {
    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRE_MINUTES = 5;
    private static final String TYPE_VERIFY = "VERIFY_EMAIL";
    private static final String TYPE_FORGOT = "FORGOT_PASSWORD";

    @Autowired private EmailOtpRepository emailOtpRepo;
    @Autowired private EmailService emailService;
    @Autowired private UserRepository userRepository;

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    @Override
    @Transactional
    public ResponseObject sendVerificationOtp(EmailRequest request) {
        String email = request.getEmail();
        // Chỉ gửi OTP xác thực cho email đã đăng ký
        if (!userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email not found");
        }
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found with email: " + email));
        if (user.isVerify()) {
            throw new BadRequestException("Email has already been verified");
        }
        emailOtpRepo.deleteByEmailAndType(email, TYPE_VERIFY);
        String otp = generateOtp();
        EmailOtp entity = EmailOtp.builder()
                .email(email)
                .otp(otp)
                .type(TYPE_VERIFY)
                .expiredAt(LocalDateTime.now().plusMinutes(OTP_EXPIRE_MINUTES))
                .verified(false)
                .createdAt(LocalDateTime.now())
                .build();
        emailOtpRepo.save(entity);
        emailService.sendEmail(email, "Email Verification OTP", "Your OTP is: " + otp);
        return new com.example.demologin.dto.response.ResponseObject(200, "OTP sent successfully", null);
    }

    @Override
    @Transactional
    public ResponseObject verifyEmailOtp(OtpRequest request) {
        EmailOtp otpEntity = emailOtpRepo.findTopByEmailAndTypeOrderByCreatedAtDesc(request.getEmail(), TYPE_VERIFY)
                .orElseThrow(() -> new BadRequestException("No OTP found for this email"));
        if (otpEntity.isVerified()) throw new BadRequestException("OTP already used");
        if (otpEntity.getExpiredAt().isBefore(LocalDateTime.now())) throw new BadRequestException("OTP expired");
        if (!otpEntity.getOtp().equals(request.getOtp())) throw new BadRequestException("OTP invalid");
        otpEntity.setVerified(true);
        emailOtpRepo.save(otpEntity);
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            user.setVerify(true);
            userRepository.save(user);
        });
        // Xóa OTP sau khi dùng xong
        emailOtpRepo.deleteById(otpEntity.getId());
        log.debug("OTP deleted after successful verification for email: {}", request.getEmail());
        return new com.example.demologin.dto.response.ResponseObject(200, "OTP verified successfully", null);
    }

    @Override
    @Transactional
    public ResponseObject sendForgotPasswordOtp(EmailRequest request) {
        String email = request.getEmail();
        if (!userRepository.existsByEmail(email)) throw new BadRequestException("Email not found");
        emailOtpRepo.deleteByEmailAndType(email, TYPE_FORGOT);
        String otp = generateOtp();
        EmailOtp entity = EmailOtp.builder()
                .email(email)
                .otp(otp)
                .type(TYPE_FORGOT)
                .expiredAt(LocalDateTime.now().plusMinutes(OTP_EXPIRE_MINUTES))
                .verified(false)
                .createdAt(LocalDateTime.now())
                .build();
        emailOtpRepo.save(entity);
        emailService.sendEmail(email, "Forgot Password OTP", "Your OTP is: " + otp);
        return new com.example.demologin.dto.response.ResponseObject(200, "Forgot password OTP sent successfully", null);
    }

    @Override
    @Transactional
    public ResponseObject resetPasswordWithOtp(ResetPasswordRequestWithOtp req) {
        EmailOtp otpEntity = emailOtpRepo.findTopByEmailAndTypeOrderByCreatedAtDesc(req.getEmail(), TYPE_FORGOT)
                .orElseThrow(() -> new BadRequestException("No OTP found for this email"));
        if (otpEntity.isVerified()) throw new BadRequestException("OTP already used");
        if (otpEntity.getExpiredAt().isBefore(LocalDateTime.now())) throw new BadRequestException("OTP expired");
        if (!otpEntity.getOtp().equals(req.getOtp())) throw new BadRequestException("OTP invalid");
        otpEntity.setVerified(true);
        emailOtpRepo.save(otpEntity);
        User user = userRepository.findByEmail(req.getEmail()).orElseThrow(() -> new BadRequestException("User not found"));
        user.setPassword(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(req.getNewPassword()));
        userRepository.save(user);
        emailOtpRepo.deleteById(otpEntity.getId());
        log.debug("OTP deleted after successful password reset for email: {}", req.getEmail());
        return new ResponseObject(200, "Password reset successfully", null);
    }

    @Override
    @Transactional
    public ResponseObject resendOtp(EmailRequest request) {
        // Xác định loại OTP cần resend (ở đây mặc định resend cho VERIFY_EMAIL)
        return sendVerificationOtp(request);
    }

    // Xóa OTP hết hạn định kỳ
    @Scheduled(fixedRate = 5 * 60 * 1000) // mỗi 5 phút
    @Transactional
    public void deleteExpiredOtps() {
        LocalDateTime now = LocalDateTime.now();
        
        // Kiểm tra trước xem có OTP hết hạn không
        long expiredCount = emailOtpRepo.countExpiredOtps(now);
        
        if (expiredCount > 0) {
            // Chỉ thực hiện delete khi có OTP cần xóa
            int deletedCount = emailOtpRepo.deleteExpiredOtps(now);
            log.info("Deleted {} expired OTP records", deletedCount);
        }
        // Không log gì khi không có OTP nào cần xóa
    }
} 