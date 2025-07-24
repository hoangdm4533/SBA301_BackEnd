package com.example.demologin.service;

import com.example.demologin.dto.request.FacebookLoginRequest;
import com.example.demologin.dto.request.GoogleLoginRequest;
import com.example.demologin.dto.request.LoginRequest;
import com.example.demologin.dto.request.UserRegistrationRequest;
import com.example.demologin.dto.response.UserResponse;
import com.example.demologin.entity.PasswordResetToken;
import com.example.demologin.entity.RefreshToken;
import com.example.demologin.entity.User;
import com.example.demologin.entity.UserActivityLog;
import com.example.demologin.enums.ActivityType;
import com.example.demologin.enums.Gender;
import com.example.demologin.enums.UserStatus;
import com.example.demologin.exception.exceptions.NotFoundException;
import com.example.demologin.mapper.UserMapper;
import com.example.demologin.repository.PasswordResetTokenRepository;
import com.example.demologin.repository.RefreshTokenRepository;
import com.example.demologin.repository.UserActivityLogRepository;
import com.example.demologin.repository.UserRepository;
import com.example.demologin.repository.RoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public interface AuthenticationService extends UserDetailsService {
    User register(UserRegistrationRequest request);
    UserResponse login(LoginRequest loginRequest);
    void createPasswordResetTokenForAccount(User user, String token);
    User validatePasswordResetToken(String token);
    void changePassword(User user, String newPassword);
    void deleteResetToken(String token);
    UserResponse authenticateWithGoogle(GoogleLoginRequest request);
    UserResponse authenticateWithOAuth2(String email, String name);
    UserResponse authenticateWithFacebook(FacebookLoginRequest request);
    UserResponse getUserResponse(String email, String name);
}