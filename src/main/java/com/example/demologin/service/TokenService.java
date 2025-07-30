package com.example.demologin.service;

import com.example.demologin.entity.User;
import com.example.demologin.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public interface TokenService {
    String generateToken(User user);
    String getUsernameFromToken(String token);
    User getAccountByToken(String token);
    boolean validateToken(String authToken);
    
    /**
     * Extract permissions from JWT token.
     * 
     * @param token JWT token (without "Bearer " prefix)
     * @return Set of permission codes
     * @throws com.example.demologin.exception.TokenValidationException if token is invalid
     */
    Set<String> extractPermissions(String token);
    
    /**
     * Extract username/subject from JWT token with detailed error handling.
     * 
     * @param token JWT token (without "Bearer " prefix)
     * @return username/subject
     * @throws com.example.demologin.exception.TokenValidationException if token is invalid
     */
    String extractUsernameWithValidation(String token);
}
