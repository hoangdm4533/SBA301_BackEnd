package com.example.demologin.serviceImpl;

import com.example.demologin.entity.User;
import com.example.demologin.exception.TokenValidationException;
import com.example.demologin.exception.exceptions.NotFoundException;
import com.example.demologin.repository.UserRepository;
import com.example.demologin.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class TokenServiceImpl implements TokenService {
    @Autowired
    UserRepository userRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration.ms}")
    private int jwtExpirationMs;

    private SecretKey getSigninKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("tokenVersion", user.getTokenVersion());
        claims.put("permissionCodes", user.getPermissionCodes());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSigninKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    @Override
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigninKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    @Override
    public User getAccountByToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigninKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        String username = claims.getSubject();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Account not found with username: " + username));
        int tokenVersion = (int) claims.get("tokenVersion");
        if (tokenVersion != user.getTokenVersion()) {
            throw new ExpiredJwtException(null, claims, "Token has been invalidated");
        }
        return user;
    }

    @Override
    public boolean validateToken(String authToken) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigninKey())
                    .build()
                    .parseSignedClaims(authToken)
                    .getPayload();
            User user = userRepository.findByUsername(claims.getSubject())
                    .orElseThrow(() -> new NotFoundException("User not found"));
            return (int) claims.get("tokenVersion") == user.getTokenVersion();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Set<String> extractPermissions(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigninKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            
            Object codesObj = claims.get("permissionCodes");
            Set<String> permissionCodes;
            
            if (codesObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> codesList = (List<String>) codesObj;
                permissionCodes = new HashSet<>(codesList);
            } else if (codesObj instanceof Set) {
                @SuppressWarnings("unchecked")
                Set<String> codesSet = (Set<String>) codesObj;
                permissionCodes = codesSet;
            } else {
                log.warn("Invalid permission format in token for user: {}", claims.getSubject());
                throw new TokenValidationException("Invalid permission format in token", 
                    TokenValidationException.TokenErrorType.MISSING_PERMISSIONS);
            }
            
            return permissionCodes;
            
        } catch (ExpiredJwtException e) {
            log.warn("Expired token used by user: {}", e.getClaims().getSubject());
            throw new TokenValidationException("Token expired", 
                TokenValidationException.TokenErrorType.EXPIRED, e);
        } catch (JwtException e) {
            log.warn("Invalid token: {}", e.getMessage());
            throw new TokenValidationException("Token invalid", 
                TokenValidationException.TokenErrorType.INVALID_SIGNATURE, e);
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            throw new TokenValidationException("Token validation failed", 
                TokenValidationException.TokenErrorType.GENERAL_ERROR, e);
        }
    }

    @Override
    public String extractUsernameWithValidation(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigninKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            
            return claims.getSubject();
            
        } catch (ExpiredJwtException e) {
            log.warn("Expired token used by user: {}", e.getClaims().getSubject());
            throw new TokenValidationException("Token expired", 
                TokenValidationException.TokenErrorType.EXPIRED, e);
        } catch (JwtException e) {
            log.warn("Invalid token: {}", e.getMessage());
            throw new TokenValidationException("Token invalid", 
                TokenValidationException.TokenErrorType.INVALID_SIGNATURE, e);
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            throw new TokenValidationException("Token validation failed", 
                TokenValidationException.TokenErrorType.GENERAL_ERROR, e);
        }
    }
} 