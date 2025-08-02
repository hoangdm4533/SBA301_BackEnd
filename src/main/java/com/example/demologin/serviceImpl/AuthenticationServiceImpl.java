package com.example.demologin.serviceImpl;

import com.example.demologin.dto.request.login.FacebookLoginRequest;
import com.example.demologin.dto.request.login.GoogleLoginRequest;
import com.example.demologin.dto.request.login.LoginRequest;
import com.example.demologin.dto.request.user.UserRegistrationRequest;
import com.example.demologin.dto.response.LoginResponse;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.dto.response.UserResponse;
import com.example.demologin.entity.PasswordResetToken;
import com.example.demologin.entity.RefreshToken;
import com.example.demologin.entity.User;
import com.example.demologin.entity.UserActivityLog;
import com.example.demologin.enums.ActivityType;
import com.example.demologin.enums.Gender;
import com.example.demologin.enums.UserStatus;
import com.example.demologin.exception.exceptions.*;
import com.example.demologin.mapper.UserMapper;
import com.example.demologin.repository.PasswordResetTokenRepository;
import com.example.demologin.repository.RefreshTokenRepository;
import com.example.demologin.repository.UserActivityLogRepository;
import com.example.demologin.repository.UserRepository;
import com.example.demologin.repository.RoleRepository;
import com.example.demologin.service.AuthenticationService;
import com.example.demologin.service.BruteForceProtectionService;
import com.example.demologin.utils.IpUtils;
import com.example.demologin.utils.EmailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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

@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String GOOGLE_CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.facebook.client-id}")
    private String FACEBOOK_CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.facebook.client-secret}")
    private String FACEBOOK_CLIENT_SECRET;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    com.example.demologin.service.TokenService tokenService;

    @Autowired
    com.example.demologin.service.RefreshTokenService refreshTokenService;

    @Autowired
    UserMapper userMapper;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    UserActivityLogRepository userActivityLogRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BruteForceProtectionService bruteForceProtectionService;

    @Override
    public UserResponse register(UserRegistrationRequest request) {
        UserActivityLog log = null;
        try {
            // Business logic validation
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                throw new ValidationException("Password and Confirm Password do not match");
            }
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new ConflictException("Username already exists");
            }
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ConflictException("Email already exists");
            }
            
            // Role validation
            com.example.demologin.entity.Role memberRole = roleRepository.findByName("MEMBER")
                .orElseThrow(() -> new NotFoundException("Role MEMBER not found"));
            Set<com.example.demologin.entity.Role> roles = new HashSet<>();
            roles.add(memberRole);
            
            User newUser = new User(
                    request.getUsername(),
                    passwordEncoder.encode(request.getPassword()),
                    request.getFullName(),
                    request.getEmail(),
                    request.getPhone(),
                    request.getAddress()
            );
            
            // Set fields
            newUser.setDateOfBirth(request.getDateOfBirth());
            newUser.setGender(request.getGender());
            newUser.setIdentityCard(request.getIdentityCard());
            newUser.setRoles(roles);
            newUser.setStatus(UserStatus.ACTIVE);
            
            User savedUser = userRepository.save(newUser);
            
            log = UserActivityLog.builder()
                    .activityType(ActivityType.REGISTRATION)
                    .userId(savedUser.getUserId())
                    .timestamp(LocalDateTime.now())
                    .status("SUCCESS")
                    .details("New user registered: " + savedUser.getUsername())
                    .build();
            return UserMapper.toResponse(savedUser, "", "");
        } catch (ConflictException | ValidationException e) {
            log = UserActivityLog.builder()
                    .activityType(ActivityType.REGISTRATION)
                    .timestamp(LocalDateTime.now())
                    .status("FAIL")
                    .details("Registration failed: " + e.getMessage())
                    .build();
            throw e;
        } catch (Exception e) {
            log = UserActivityLog.builder()
                    .activityType(ActivityType.REGISTRATION)
                    .timestamp(LocalDateTime.now())
                    .status("FAIL")
                    .details("Registration failed: " + e.getMessage())
                    .build();
            throw new InternalServerErrorException("Registration failed: " + e.getMessage());
        } finally {
            if (log != null) {
                userActivityLogRepository.save(log);
            }
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Kiểm tra xem input có phải là email hợp lệ không
        if (EmailUtils.isValidEmail(username)) {
            // Tìm user bằng email
            return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Account not found with email: " + username));
        } else {
            // Tìm user bằng username
            return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Account not found with username: " + username));
        }
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        String clientIp = IpUtils.getClientIpAddress();
        String username = loginRequest.getUsername();
        
        // Check if account is locked due to brute force attempts
        if (bruteForceProtectionService.isAccountLocked(username)) {
            long remainingMinutes = bruteForceProtectionService.getRemainingLockoutMinutes(username);
            
            String message = String.format("Tài khoản của bạn đã bị tạm khóa. Vui lòng thử lại sau %d phút.", remainingMinutes);
            
            // Record the failed attempt due to account being locked
            bruteForceProtectionService.recordLoginAttempt(username, clientIp, false, "Account locked due to brute force protection");
            
            throw new AccountLockedException(message, remainingMinutes);
        }
        
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            username,
                            loginRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            // Handle failed login attempt
            bruteForceProtectionService.handleFailedLogin(username, clientIp, "Invalid credentials");
            throw new UnauthorizedException("Username/ password is invalid. Please try again!");
        } catch (LockedException e) {
            // Handle account locked (not brute force related)
            bruteForceProtectionService.recordLoginAttempt(username, clientIp, false, "Account manually locked");
            throw new ForbiddenException("Account has been locked!");
        } catch (Exception e) {
            // Handle other authentication errors
            bruteForceProtectionService.handleFailedLogin(username, clientIp, "Authentication error: " + e.getMessage());
            throw new InternalServerErrorException("Login failed: " + e.getMessage());
        }

        User user = (User) authentication.getPrincipal();

        if (!user.isVerify()) {
            bruteForceProtectionService.recordLoginAttempt(username, clientIp, false, "Account not verified");
            throw new ForbiddenException("Account has not been verified yet. Please verify your email.");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            bruteForceProtectionService.recordLoginAttempt(username, clientIp, false, "Account not active");
            throw new ForbiddenException("Account is not active.");
        }

        // Login successful - handle successful login
        bruteForceProtectionService.handleSuccessfulLogin(username, clientIp);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        String token = tokenService.generateTokenForUser(user);
        LoginResponse loginResponse = UserMapper.toLoginResponse(user, token, refreshToken.getToken());
        return loginResponse;
    }

    @Override
    public void logout() {
        Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            throw new UnauthorizedException("User not authenticated");
        }
        
        User user = (User) authentication.getPrincipal();
        user.incrementTokenVersion();
        userRepository.save(user);
        refreshTokenRepository.deleteByUser(user);
    }


    @Override
    public void createPasswordResetTokenForAccount(User user, String token) {
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(calculateExpiryDate(60 * 60));
        passwordResetTokenRepository.save(resetToken);
    }

    private Date calculateExpiryDate(int expiryTimeInSeconds) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, expiryTimeInSeconds);
        return new Date(cal.getTime().getTime());
    }

    @Override
    public User validatePasswordResetToken(String token) {
        PasswordResetToken passToken = passwordResetTokenRepository.findByToken(token);
        if (passToken == null) {
            throw new NotFoundException("Invalid token");
        }
        if (passToken.getExpiryDate().before(new Date())) {
            throw new BadRequestException("Token expired");
        }
        return passToken.getUser();
    }

    @Override
    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void deleteResetToken(String token) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);
        if (resetToken != null) {
            passwordResetTokenRepository.delete(resetToken);
        }
    }

    @Override
    public UserResponse authenticateWithGoogle(GoogleLoginRequest request) {
        try {
            log.debug("Attempting to verify Google token: {}", request.getIdToken().substring(0, Math.min(10, request.getIdToken().length())) + "...");
            if (request.getIdToken().startsWith("ya29.")) {
                log.debug("Detected Google access token, using Google API to get user info");
                return authenticateWithGoogleAccessToken(request.getIdToken());
            }
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(java.util.Collections.singletonList(GOOGLE_CLIENT_ID))
                    .build();
            GoogleIdToken idToken = verifier.verify(request.getIdToken());
            if (idToken == null) {
                log.error("Google ID token verification failed");
                throw new UnauthorizedException("Invalid Google ID token");
            }
            
            Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                Set<com.example.demologin.entity.Role> roles = new HashSet<>();
                roles.add(roleRepository.findByName("MEMBER").orElseThrow(() -> new NotFoundException("Role MEMBER not found")));
                
                user = new User(
                        email.substring(0, email.indexOf('@')),
                        passwordEncoder.encode(""),
                        name != null ? name : "",
                        email,
                        "",
                        ""
                );
                
                user.setRoles(roles);
                user.setStatus(UserStatus.ACTIVE);
                user.setCreatedAt(LocalDateTime.now());
                user.setIdentityCard("");
                user.setDateOfBirth(LocalDateTime.now().toLocalDate());
                user.setGender(com.example.demologin.enums.Gender.OTHER);
                
                user = userRepository.save(user);
            }
            
            String token = tokenService.generateTokenForUser(user);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
            
            return UserMapper.toResponse(user, token, refreshToken.getToken());
        } catch (BadRequestException | UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error authenticating with Google", e);
            throw new InternalServerErrorException("Google authentication failed: " + e.getMessage());
        }
    }

    private UserResponse authenticateWithGoogleAccessToken(String accessToken) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    "https://www.googleapis.com/oauth2/v3/userinfo",
                    HttpMethod.GET,
                    entity,
                    (Class<Map<String, Object>>) (Object) Map.class
            );
            Map<String, Object> userInfo = response.getBody();
            if (userInfo == null) {
                throw new UnauthorizedException("Failed to get user info from Google API");
            }
            
            log.debug("Retrieved user info from Google API: {}", userInfo);
            String email = (String) userInfo.get("email");
            String name = (String) userInfo.get("name");
            
            return authenticateWithOAuth2(email, name);
        } catch (Exception e) {
            log.error("Error authenticating with Google access token", e);
            throw new InternalServerErrorException("Google authentication failed: " + e.getMessage());
        }
    }

    @Override
    public UserResponse authenticateWithOAuth2(String email, String name) {
        try {
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                Set<com.example.demologin.entity.Role> roles = new HashSet<>();
                roles.add(roleRepository.findByName("MEMBER").orElseThrow(() -> new NotFoundException("Role MEMBER not found")));
                
                user = new User(
                        email.substring(0, email.indexOf('@')),
                        passwordEncoder.encode(""),
                        name != null ? name : "",
                        email,
                        "",
                        ""
                );
                
                user.setRoles(roles);
                user.setStatus(UserStatus.ACTIVE);
                user.setCreatedAt(LocalDateTime.now());
                user.setIdentityCard("");
                user.setVerify(true);
                user.setDateOfBirth(LocalDateTime.now().toLocalDate());
                user.setGender(com.example.demologin.enums.Gender.OTHER);
                
                user = userRepository.save(user);
            }
            
            String token = tokenService.generateTokenForUser(user);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
            
            return UserMapper.toResponse(user, token, refreshToken.getToken());
        } catch (Exception e) {
            log.error("Error authenticating with OAuth2", e);
            throw new InternalServerErrorException("OAuth2 authentication failed: " + e.getMessage());
        }
    }

    @Override
    public UserResponse authenticateWithOAuth2FromAuthentication(org.springframework.security.core.Authentication authentication) {
        if (!(authentication instanceof org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken)) {
            throw new BadRequestException("Not an OAuth2 authentication");
        }
        
        org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken oauthToken = 
            (org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken) authentication;
        
        org.springframework.security.oauth2.core.user.OAuth2User oAuth2User = oauthToken.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        return authenticateWithOAuth2(email, name != null ? name : email);
    }

    @Override
    public void handleOAuth2Failure() {
        throw new UnauthorizedException("OAuth2 login failed");
    }

    @Override
    public UserResponse authenticateWithFacebook(FacebookLoginRequest request) {
        try {
            log.debug("Attempting to verify Facebook token: {}", request.getAccessToken().substring(0, Math.min(10, request.getAccessToken().length())) + "...");
            String fields = "id,name,email,first_name,last_name,picture,gender,birthday,location";
            String url = String.format(
                    "https://graph.facebook.com/me?fields=%s&access_token=%s",
                    fields,
                    request.getAccessToken()
            );
            RestTemplate restTemplate = new RestTemplate();
            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    (Class<Map<String, Object>>) (Object) Map.class
            );
            Map<String, Object> userInfo = response.getBody();
            if (userInfo == null) {
                throw new UnauthorizedException("Failed to get user info from Facebook API");
            }
            
            log.debug("Retrieved user info from Facebook API: {}", userInfo);
            String email = (String) userInfo.get("email");
            String name = (String) userInfo.get("name");
            String firstName = (String) userInfo.get("first_name");
            String lastName = (String) userInfo.get("last_name");
            String gender = (String) userInfo.get("gender");
            String birthday = (String) userInfo.get("birthday");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> picture = (Map<String, Object>) userInfo.get("picture");
            String pictureUrl = null;
            if (picture != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) picture.get("data");
                if (data != null) {
                    pictureUrl = (String) data.get("url");
                }
            }
            
            @SuppressWarnings("unchecked")
            Map<String, Object> location = (Map<String, Object>) userInfo.get("location");
            String locationName = location != null ? (String) location.get("name") : null;
            
            if (email == null || email.trim().isEmpty()) {
                String userId = (String) userInfo.get("id");
                email = userId != null ? userId + "@facebook.com" : "unknown@facebook.com";
            }
            
            return authenticateWithFacebookOAuth2(
                    email,
                    name,
                    firstName,
                    lastName,
                    gender,
                    birthday,
                    pictureUrl,
                    locationName
            );
        } catch (Exception e) {
            log.error("Error authenticating with Facebook", e);
            throw new InternalServerErrorException("Facebook authentication failed: " + e.getMessage());
        }
    }

    private UserResponse authenticateWithFacebookOAuth2(
            String email,
            String name,
            String firstName,
            String lastName,
            String gender,
            String birthday,
            String pictureUrl,
            String location
    ) {
        try {
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                Set<com.example.demologin.entity.Role> roles = new HashSet<>();
                roles.add(roleRepository.findByName("MEMBER").orElseThrow(() -> new NotFoundException("Role MEMBER not found")));
                
                user = new User(
                        email.substring(0, email.indexOf('@')),
                        passwordEncoder.encode(""),
                        name != null ? name : (firstName + " " + lastName),
                        email,
                        "",
                        location != null ? location : ""
                );
                
                user.setRoles(roles);
                user.setStatus(UserStatus.ACTIVE);
                user.setCreatedAt(LocalDateTime.now());
                user.setIdentityCard("");
                user.setDateOfBirth(parseBirthday(birthday));
                user.setGender(parseGender(gender));
                user.setVerify(true);
                
                user = userRepository.save(user);
            } else {
                boolean updated = false;
                if (name != null && !name.equals(user.getFullName())) {
                    user.setFullName(name);
                    updated = true;
                }
                if (location != null && !location.equals(user.getAddress())) {
                    user.setAddress(location);
                    updated = true;
                }
                if (!user.getRoles().stream().anyMatch(r -> r.getName().equals("MEMBER"))) {
                    user.addRole(roleRepository.findByName("MEMBER").orElseThrow(() -> new NotFoundException("Role MEMBER not found")));
                    updated = true;
                }
                if (user.getStatus() != UserStatus.ACTIVE) {
                    user.setStatus(UserStatus.ACTIVE);
                    updated = true;
                }
                if (updated) {
                    user = userRepository.save(user);
                }
            }
            
            String token = tokenService.generateTokenForUser(user);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
            
            return UserMapper.toResponse(user, token, refreshToken.getToken());
        } catch (Exception e) {
            log.error("Error authenticating with Facebook OAuth2", e);
            throw new InternalServerErrorException("Facebook OAuth2 authentication failed: " + e.getMessage());
        }
    }

    private LocalDate parseBirthday(String birthday) {
        if (birthday == null || birthday.isEmpty()) {
            return LocalDate.now().minusYears(18);
        }
        try {
            if (birthday.contains("/")) {
                String[] parts = birthday.split("/");
                int month = Integer.parseInt(parts[0]);
                int day = Integer.parseInt(parts[1]);
                int year = Integer.parseInt(parts[2]);
                return LocalDate.of(year, month, day);
            } else if (birthday.contains("-")) {
                return LocalDate.parse(birthday);
            }
        } catch (Exception e) {
            log.warn("Failed to parse birthday: {}", birthday, e);
        }
        return LocalDate.now().minusYears(18);
    }

    private Gender parseGender(String gender) {
        if (gender == null || gender.isEmpty()) {
            return Gender.OTHER;
        }
        switch (gender.toLowerCase()) {
            case "male": return Gender.MALE;
            case "female": return Gender.FEMALE;
            default: return Gender.OTHER;
        }
    }

    @Override
    public UserResponse getUserResponse(String email, String name) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            Set<com.example.demologin.entity.Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName("MEMBER").orElseThrow(() -> new NotFoundException("Role MEMBER not found")));
            
            user = new User(
                    email.substring(0, email.indexOf('@')),
                    passwordEncoder.encode(""),
                    name != null ? name : "",
                    email,
                    "",
                    ""
            );
            
            user.setStatus(UserStatus.ACTIVE);
            user.setCreatedAt(LocalDateTime.now());
            user.setIdentityCard("");
            user.setDateOfBirth(LocalDateTime.now().toLocalDate());
            user.setGender(Gender.OTHER);
            user.setRoles(roles);
            user.setVerify(true);
            
            user = userRepository.save(user);
        }
        
        String token = tokenService.generateTokenForUser(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        
        return UserMapper.toResponse(user, token, refreshToken.getToken());
    }
} 
