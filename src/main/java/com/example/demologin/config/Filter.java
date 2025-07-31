package com.example.demologin.config;

import com.example.demologin.entity.User;
import com.example.demologin.exception.exceptions.AuthorizeException;
import com.example.demologin.service.TokenService;
import com.example.demologin.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demologin.dto.response.ResponseObject;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.List;

@Component
public class Filter extends OncePerRequestFilter {

    @Autowired
    @Qualifier("handlerExceptionResolver")
    HandlerExceptionResolver resolver;

    @Autowired
    private TokenService tokenService;
    
    @Autowired
    private JwtUtil jwtUtil;

    // Danh sách các API public
    List<String> PUBLIC_API = List.of(
            // Swagger endpoints
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",

            // Authentication endpoints
            "/api/login",
            "/api/register",
            "/api/refresh-token",
            "/api/google-login",
            "/api/facebook-login",
            "/api/reset-password",
            "/api/forgot-password",
            // Email OTP endpoints
            "/api/email/send-verification",
            "/api/email/verify",
            "/api/email/forgot-password",
            "/api/email/reset-password",
            "/api/email/resend",

            // OAuth2 endpoints
            "/api/oauth2/success",
            "/api/oauth2/failure",
            "/oauth2/**",
            "/login/oauth2/**",

            // Product endpoints
            "/api/product/**"
    );

    // Kiểm tra request có phải là public API không
    boolean isPermitted(HttpServletRequest request) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        String uri = request.getRequestURI();
        String method = request.getMethod();

        // Xử lý đặc biệt cho GET /api/product/**
        if (method.equals("GET") && pathMatcher.match("/api/product/**", uri)) {
            return true;
        }

        // Cho phép các request OAuth2 (thêm trường hợp fallback)
        if (uri.startsWith("/oauth2/") || uri.startsWith("/login/oauth2/")) {
            return true;
        }

        return PUBLIC_API.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, uri));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (isPermitted(request)) {
            // Cho phép request public API đi qua
            filterChain.doFilter(request, response);
            return;
        }

        // Xử lý request cần authentication
        String token = getToken(request);

        if (token == null) {
            writeAuthError(response, HttpStatus.UNAUTHORIZED.value(), "Authentication token is missing!");
            return;
        }

        try {
            // Get username from token first
            String username = jwtUtil.extractUsername(token);
            if (username == null) {
                writeAuthError(response, HttpStatus.UNAUTHORIZED.value(), "Authentication token is invalid!");
                return;
            }
            
            // Get user from token using TokenService
            User user = tokenService.getUserByToken(token);
            if (user == null) {
                writeAuthError(response, HttpStatus.UNAUTHORIZED.value(), "User not found for the provided token!");
                return;
            }
            
            // Validate token with user
            if (!jwtUtil.validateToken(token, user)) {
                writeAuthError(response, HttpStatus.UNAUTHORIZED.value(), "Authentication token is invalid!");
                return;
            }

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(user, token, user.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
            filterChain.doFilter(request, response);

        } catch (MalformedJwtException e) {
            writeAuthError(response, HttpStatus.UNAUTHORIZED.value(), "Authentication token is invalid!");
        } catch (ExpiredJwtException e) {
            writeAuthError(response, HttpStatus.UNAUTHORIZED.value(), "Authentication token is expired!");
        } catch (Exception e) {
            writeAuthError(response, HttpStatus.UNAUTHORIZED.value(), "Authentication token is invalid!");
        }
    }

    private String getToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        return (token != null && token.startsWith("Bearer ")) ? token.substring(7) : null;
    }

    private void writeAuthError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        ResponseObject resp = new ResponseObject(status, message, null);
        new ObjectMapper().writeValue(response.getWriter(), resp);
    }
}
