package com.example.demologin.utils;

import com.example.demologin.entity.User;
import com.example.demologin.exception.TokenValidationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * JWT Utility class for token operations
 * Handles JWT creation, validation, and extraction
 */
@Component
@Slf4j
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration.ms}")
    private int jwtExpirationMs;

    /**
     * Get signing key for JWT
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generate JWT token for user
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("tokenVersion", user.getTokenVersion());
        claims.put("permissionCodes", user.getPermissionCodes());
        
        return Jwts.builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extract username from JWT token
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extract username with full validation
     */
    public String extractUsernameWithValidation(String token) {
        try {
            Claims claims = extractAllClaims(token);
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

    /**
     * Extract token version from JWT
     */
    public Integer extractTokenVersion(String token) {
        Claims claims = extractAllClaims(token);
        return (Integer) claims.get("tokenVersion");
    }

    /**
     * Extract permissions from JWT token
     */
    public Set<String> extractPermissions(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Object codesObj = claims.get("permissionCodes");
            
            if (codesObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> codesList = (List<String>) codesObj;
                return new HashSet<>(codesList);
            } else if (codesObj instanceof Set) {
                @SuppressWarnings("unchecked")
                Set<String> codesSet = (Set<String>) codesObj;
                return codesSet;
            } else {
                log.warn("Invalid permission format in token for user: {}", claims.getSubject());
                throw new TokenValidationException("Invalid permission format in token", 
                    TokenValidationException.TokenErrorType.MISSING_PERMISSIONS);
            }
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

    /**
     * Extract expiration date from JWT
     */
    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    /**
     * Check if token is expired
     */
    public Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Validate JWT token structure and signature
     */
    public Boolean validateTokenStructure(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            log.debug("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validate token with token version check
     */
    public Boolean validateToken(String token, User user) {
        try {
            Claims claims = extractAllClaims(token);
            String username = claims.getSubject();
            Integer tokenVersion = (Integer) claims.get("tokenVersion");
            
            return username.equals(user.getUsername()) 
                && tokenVersion.equals(user.getTokenVersion())
                && !isTokenExpired(token);
        } catch (Exception e) {
            log.debug("Token validation failed for user {}: {}", user.getUsername(), e.getMessage());
            return false;
        }
    }

    /**
     * Extract all claims from JWT token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
