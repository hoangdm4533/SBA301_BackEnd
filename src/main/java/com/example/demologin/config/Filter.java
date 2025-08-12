package com.example.demologin.config;

import com.example.demologin.entity.User;
import com.example.demologin.exception.exceptions.InvalidTokenException;
import com.example.demologin.exception.exceptions.UnauthorizedException;
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
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class Filter extends OncePerRequestFilter {


    private final TokenService tokenService;

    private final JwtUtil jwtUtil;

    private final PublicEndpointHandlerMapping publicEndpointHandlerMapping;

    // Kiểm tra request có phải là public API không
    boolean isPermitted(HttpServletRequest request) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        String uri = request.getRequestURI();

        // Lấy danh sách các public endpoints từ annotation @PublicEndpoint
        List<String> annotatedPublicEndpoints = publicEndpointHandlerMapping.getPublicEndpoints();
        
        // Kiểm tra các endpoint được đánh dấu @PublicEndpoint
        boolean isAnnotatedPublic = annotatedPublicEndpoints.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, uri));
        
        if (isAnnotatedPublic) {
            return true;
        }

        // Các endpoint hệ thống cần permit all (giống như trong SecurityConfig)
        List<String> systemPublicEndpoints = List.of(
            // Swagger/OpenAPI documentation
            "/swagger-ui/**",
            "/v3/api-docs/**", 
            "/swagger-resources/**",
            "/webjars/**",
            // OAuth2 system endpoints (Spring Security tự động tạo)
            "/login/oauth2/code/**",
            "/oauth2/authorization/**"
        );

        // Kiểm tra các endpoint hệ thống
        boolean isSystemPublic = systemPublicEndpoints.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, uri));

        return isSystemPublic;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (isPermitted(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getToken(request);
        if (token == null) {
            throw new UnauthorizedException("Authentication token is missing!");
        }

        String username = jwtUtil.extractUsername(token);
        if (username == null || username.isBlank()) {
            throw new InvalidTokenException("Authentication token is invalid!");
        }

        User user = tokenService.getUserByToken(token);
        if (user == null) {
            throw new UnauthorizedException("User not found for the provided token!");
        }

        if (!jwtUtil.validateToken(token, user)) {
            throw new InvalidTokenException("Authentication token is invalid!");
        }

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(user, token, user.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }


    private String getToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        return (token != null && token.startsWith("Bearer ")) ? token.substring(7) : null;
    }

}
