package com.example.demologin.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomOAuth2FailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        org.springframework.security.core.AuthenticationException exception)
            throws IOException, ServletException {

        // Ghi log nếu cần
        System.err.println("OAuth2 login failed: " + exception.getMessage());

        // Redirect về trang login frontend cùng lỗi
        String errorMessage = exception.getMessage().replace(" ", "_"); // cho URL an toàn
        response.sendRedirect("http://localhost:3000/login?error=" + errorMessage);
    }
}
