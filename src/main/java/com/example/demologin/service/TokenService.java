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

public interface TokenService {
    String generateToken(User user);
    String getUsernameFromToken(String token);
    User getAccountByToken(String token);
    boolean validateToken(String authToken);
}
