package com.example.demologin.config;

import com.example.demologin.entity.User;
import com.example.demologin.exception.exceptions.AuthorizeException;
import com.example.demologin.service.TokenService;
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

import java.io.IOException;
import java.util.List;

@Component
public class Filter extends OncePerRequestFilter {

    @Autowired
    @Qualifier("handlerExceptionResolver")
    HandlerExceptionResolver resolver;

    @Autowired
    TokenService tokenService;

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

            // OAuth2 endpoints
            "/api/oauth2/success",
            "/api/oauth2/failure",
            "/oauth2/authorization/**",
            "/login/oauth2/code/**",

            // Product endpoints
            "/api/product/**"
    );

    // Kiểm tra request có phải là public API không
    boolean isPermitted(HttpServletRequest request) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        String uri = request.getRequestURI();
        String method = request.getMethod();

        // Đặc biệt xử lý GET /api/product/** nếu cần logic riêng
        if (method.equals("GET") && pathMatcher.match("/api/product/**", uri)) {
            return true;
        }

        // Kiểm tra trong danh sách PUBLIC_API
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
        } else {
            // Xử lý request cần authentication
            String token = getToken(request);

            if (token == null) {
                resolver.resolveException(request, response, null,
                        new AuthorizeException("Authentication token is missing!"));
                return;
            }

            try {
                User user = tokenService.getAccountByToken(token);
                if (user == null) {
                    resolver.resolveException(request, response, null,
                            new AuthorizeException("User not found for the provided token!"));
                    return;
                }

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(user, token, user.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                filterChain.doFilter(request, response);

            } catch (MalformedJwtException e) {
                resolver.resolveException(request, response, null,
                        new AuthorizeException("Authentication token is invalid!"));
            } catch (ExpiredJwtException e) {
                resolver.resolveException(request, response, null,
                        new AuthorizeException("Authentication token is expired!"));
            } catch (Exception e) {
                resolver.resolveException(request, response, null,
                        new AuthorizeException("Authentication token is invalid!"));
            }
        }
    }

    private String getToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        return (token != null && token.startsWith("Bearer ")) ? token.substring(7) : null;
    }
}