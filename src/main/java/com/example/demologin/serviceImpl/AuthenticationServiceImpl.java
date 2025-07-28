package com.example.demologin.serviceImpl;

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
import com.example.demologin.exception.exceptions.*;
import com.example.demologin.mapper.UserMapper;
import com.example.demologin.repository.PasswordResetTokenRepository;
import com.example.demologin.repository.RefreshTokenRepository;
import com.example.demologin.repository.UserActivityLogRepository;
import com.example.demologin.repository.UserRepository;
import com.example.demologin.repository.RoleRepository;
import com.example.demologin.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public User register(UserRegistrationRequest request) {
        UserActivityLog log = null;
        try {
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                throw new ValidationException("Password and Confirm Password do not match");
            }
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new ConflictException("Username already exists");
            }
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ConflictException("Email already exists");
            }
            Set<com.example.demologin.entity.Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName("MEMBER").orElseThrow(() -> new NotFoundException("Role MEMBER not found")));
            User newUser = User.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .fullName(request.getFullName())
                    .dateOfBirth(request.getDateOfBirth())
                    .gender(request.getGender())
                    .email(request.getEmail())
                    .identityCard(request.getIdentityCard())
                    .phone(request.getPhone())
                    .address(request.getAddress())
                    .roles(roles)
                    .status(UserStatus.ACTIVE)
                    .build();
            User savedUser = userRepository.save(newUser);
            log = UserActivityLog.builder()
                    .activityType(ActivityType.REGISTRATION)
                    .userId(savedUser.getUserId())
                    .timestamp(LocalDateTime.now())
                    .status("SUCCESS")
                    .details("New user registered: " + savedUser.getUsername())
                    .build();
            return savedUser;
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
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Account not found"));
    }

    @Override
    public UserResponse login(LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Username/ password is invalid. Please try again!");
        } catch (LockedException e) {
            throw new ForbiddenException("Account has been locked!");
        } catch (Exception e) {
            throw new InternalServerErrorException("Login failed: " + e.getMessage());
        }

        User user = (User) authentication.getPrincipal();

        if (!user.isVerify()) {
            throw new ForbiddenException("Account has not been verified yet. Please verify your email.");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ForbiddenException("Account is not active.");
        }

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        String token = tokenService.generateToken(user);
        return UserMapper.toResponse(user, token, refreshToken.getToken());
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
        passwordResetTokenRepository.delete(resetToken);
    }

    @Override
    public UserResponse authenticateWithGoogle(GoogleLoginRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body cannot be null");
        }
        
        if (request.getIdToken() == null || request.getIdToken().trim().isEmpty()) {
            throw new BadRequestException("Google ID token cannot be null or empty");
        }
        
        try {
            log.debug("Attempting to verify Google token: {}", request.getIdToken().substring(0, Math.min(10, request.getIdToken().length())) + "...");
            if (request.getIdToken().startsWith("ya29.")) {
                log.debug("Detected Google access token, using Google API to get user info");
                return authenticateWithGoogleAccessToken(request.getIdToken());
            }
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(java.util.Collections.singletonList(GOOGLE_CLIENT_ID))
                    .build();
            GoogleIdToken idToken;
            try {
                idToken = verifier.verify(request.getIdToken());
                if (idToken == null) {
                    log.error("Google ID token verification failed");
                    throw new UnauthorizedException("Invalid Google ID token");
                }
            } catch (java.lang.IllegalArgumentException e) {
                log.error("Invalid token format: {}", e.getMessage());
                throw new BadRequestException("Invalid token format: " + e.getMessage());
            }
            Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            User user = userRepository.findByEmail(email)
                    .orElse(null);
            if (user == null) {
                Set<com.example.demologin.entity.Role> roles = new HashSet<>();
                roles.add(roleRepository.findByName("MEMBER").orElseThrow(() -> new NotFoundException("Role MEMBER not found")));
                user = User.builder()
                        .username(email.substring(0, email.indexOf('@')))
                        .fullName(name)
                        .email(email)
                        .password(passwordEncoder.encode(""))
                        .roles(roles)
                        .status(UserStatus.ACTIVE)
                        .createdAt(LocalDateTime.now())
                        .phone("")
                        .address("")
                        .identityCard("")
                        .dateOfBirth(LocalDateTime.now().toLocalDate())
                        .gender(com.example.demologin.enums.Gender.OTHER)
                        .build();
                user = userRepository.save(user);
            }
            String token = tokenService.generateToken(user);
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
            if (email == null) {
                throw new UnauthorizedException("Email not provided by Google API");
            }
            return authenticateWithOAuth2(email, name);
        } catch (Exception e) {
            log.error("Error authenticating with Google access token", e);
            throw new InternalServerErrorException("Google authentication failed: " + e.getMessage());
        }
    }

    @Override
    public UserResponse authenticateWithOAuth2(String email, String name) {
        if (email == null) {
            throw new BadRequestException("Email not provided by OAuth2 provider");
        }
        
        try {
            User user = userRepository.findByEmail(email)
                    .orElse(null);
            if (user == null) {
                Set<com.example.demologin.entity.Role> roles = new HashSet<>();
                roles.add(roleRepository.findByName("MEMBER").orElseThrow(() -> new NotFoundException("Role MEMBER not found")));
                user = User.builder()
                        .username(email.substring(0, email.indexOf('@')))
                        .fullName(name)
                        .email(email)
                        .password(passwordEncoder.encode(""))
                        .roles(roles)
                        .status(UserStatus.ACTIVE)
                        .createdAt(LocalDateTime.now())
                        .phone("")
                        .address("")
                        .identityCard("")
                        .isVerify(true)
                        .dateOfBirth(LocalDateTime.now().toLocalDate())
                        .gender(com.example.demologin.enums.Gender.OTHER)
                        .build();
                user = userRepository.save(user);
            }
            String token = tokenService.generateToken(user);
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
        if (request == null) {
            throw new BadRequestException("Request body cannot be null");
        }
        
        if (request.getAccessToken() == null || request.getAccessToken().trim().isEmpty()) {
            throw new BadRequestException("Facebook access token cannot be null or empty");
        }
        
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
            if (email == null) {
                String userId = (String) userInfo.get("id");
                email = userId + "@facebook.com";
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
        } catch (BadRequestException | UnauthorizedException e) {
            throw e;
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
                user = User.builder()
                        .username(email.substring(0, email.indexOf('@')))
                        .fullName(name != null ? name : (firstName + " " + lastName))
                        .email(email)
                        .password(passwordEncoder.encode(""))
                        .roles(roles)
                        .status(UserStatus.ACTIVE)
                        .createdAt(LocalDateTime.now())
                        .phone("")
                        .address(location != null ? location : "")
                        .identityCard("")
                        .dateOfBirth(parseBirthday(birthday))
                        .gender(parseGender(gender))
                        .isVerify(true)
                        .build();
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
            String token = tokenService.generateToken(user);
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
            user = User.builder()
                    .username(email.substring(0, email.indexOf('@')))
                    .fullName(name)
                    .email(email)
                    .password(passwordEncoder.encode(""))
                    .status(UserStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .phone("")
                    .address("")
                    .identityCard("")
                    .dateOfBirth(LocalDateTime.now().toLocalDate())
                    .gender(Gender.OTHER)
                    .roles(roles)
                    .isVerify(true)
                    .build();
            user = userRepository.save(user);
        }
        String token = tokenService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        return UserMapper.toResponse(user, token, refreshToken.getToken());
    }
} 