package com.example.demologin.aspect;

import com.example.demologin.annotation.RequirePermission;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Set;

@Aspect
@Component
public class RequirePermissionAspect {
    @Autowired private HttpServletRequest request;
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Around("@annotation(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) throws Throwable {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) throw new RuntimeException("No token");
        token = token.substring(7);

        Claims claims = Jwts.parser()
                .verifyWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(java.util.Base64.getDecoder().decode(jwtSecret)))
                .build()
                .parseSignedClaims(token)
                .getPayload();
        Object codesObj = claims.get("permissionCodes");
        Set<String> permissionCodes;
        if (codesObj instanceof List) {
            permissionCodes = new java.util.HashSet<>((List<String>) codesObj);
        } else if (codesObj instanceof Set) {
            permissionCodes = (Set<String>) codesObj;
        } else {
            throw new RuntimeException("Invalid permissionCodes in token");
        }
        if (!permissionCodes.contains(requirePermission.value())) {
            throw new RuntimeException("Permission denied");
        }
        return joinPoint.proceed();
    }
} 