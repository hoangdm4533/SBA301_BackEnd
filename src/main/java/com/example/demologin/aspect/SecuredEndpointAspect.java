package com.example.demologin.aspect;

import com.example.demologin.annotation.SecuredEndpoint;
import com.example.demologin.exception.exceptions.TokenValidationException;
import com.example.demologin.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Aspect for handling @SecuredEndpoint annotation.
 * 
 * This aspect:
 * 1. Checks if user is authenticated
 * 2. Validates user has required permission using TokenService
 * 3. Supports both method-level and class-level annotations
 * 4. Provides detailed error logging and categorization
 * 
 * This is the primary security aspect for the application,
 * providing unified permission checking across all secured endpoints.
 */
@Aspect
@Component
@Slf4j
public class SecuredEndpointAspect {
    
    @Autowired 
    private HttpServletRequest request;
    
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Advice that runs for methods or classes annotated with @SecuredEndpoint.
     * Supports both @annotation (method-level) and @within (class-level) pointcuts.
     */
    @Around("@within(securedEndpoint) || @annotation(securedEndpoint)")
    public Object checkSecuredEndpoint(ProceedingJoinPoint joinPoint, SecuredEndpoint securedEndpoint) throws Throwable {
        
        // If annotation is at class level, get it from the class
        if (securedEndpoint == null) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            
            // Check method level first
            securedEndpoint = method.getAnnotation(SecuredEndpoint.class);
            
            // If not found on method, check class level
            if (securedEndpoint == null) {
                securedEndpoint = method.getDeclaringClass().getAnnotation(SecuredEndpoint.class);
            }
        }
        
        if (securedEndpoint == null) {
            log.warn("SecuredEndpoint annotation not found for method: {}", joinPoint.getSignature().getName());
            throw new AccessDeniedException("Security annotation missing");
        }
        
        String requiredPermission = securedEndpoint.value();
        log.debug("Checking permission: {} for method: {}", requiredPermission, joinPoint.getSignature().getName());
        
        // Check authentication first
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Unauthenticated access attempt to secured endpoint: {}", joinPoint.getSignature().getName());
            throw new AccessDeniedException("Authentication required");
        }
        
        // Extract and validate token
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for endpoint: {}", joinPoint.getSignature().getName());
            throw new AccessDeniedException("Valid Bearer token required");
        }
        
        token = token.substring(7); // Remove "Bearer " prefix
        
        try {
            // Use JwtUtil to extract permissions with detailed error handling
            Set<String> permissionCodes = jwtUtil.extractPermissions(token);
            String username = jwtUtil.extractUsernameWithValidation(token);
            
            // Check if user has required permission
            if (!permissionCodes.contains(requiredPermission)) {
                log.warn("Permission denied for user: {} - Required: {}, Available: {}", 
                    username, requiredPermission, permissionCodes);
                throw new AccessDeniedException("Permission denied: " + requiredPermission + " required");
            }
            
            log.debug("Permission check passed for user: {} with permission: {}", username, requiredPermission);
            
            // Permission check passed, proceed with method execution
            return joinPoint.proceed();
            
        } catch (TokenValidationException e) {
            // Handle specific token validation errors with appropriate logging
            switch (e.getErrorType()) {
                case EXPIRED:
                    log.warn("Token expired for endpoint: {} - {}", joinPoint.getSignature().getName(), e.getMessage());
                    throw new AccessDeniedException("Token expired");
                case INVALID_SIGNATURE:
                    log.warn("Invalid token signature for endpoint: {} - {}", joinPoint.getSignature().getName(), e.getMessage());
                    throw new AccessDeniedException("Token invalid");
                case MISSING_PERMISSIONS:
                    log.warn("Missing permissions in token for endpoint: {} - {}", joinPoint.getSignature().getName(), e.getMessage());
                    throw new AccessDeniedException("Invalid permission format in token");
                case MALFORMED:
                    log.warn("Malformed token for endpoint: {} - {}", joinPoint.getSignature().getName(), e.getMessage());
                    throw new AccessDeniedException("Token malformed");
                default:
                    log.warn("Token validation failed for endpoint: {} - {}", joinPoint.getSignature().getName(), e.getMessage());
                    throw new AccessDeniedException("Token validation failed");
            }
        } catch (RuntimeException e) {
            // Let all runtime exceptions (including business exceptions) pass through without wrapping
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during security check for endpoint: {} - {}", 
                joinPoint.getSignature().getName(), e.getMessage(), e);
            throw new AccessDeniedException("Security check failed");
        }
    }
}
