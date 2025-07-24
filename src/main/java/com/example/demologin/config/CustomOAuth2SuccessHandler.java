package com.example.demologin.config;

import com.example.demologin.dto.response.UserResponse;
import com.example.demologin.entity.User;
import com.example.demologin.enums.Gender;
import com.example.demologin.enums.Role;
import com.example.demologin.enums.UserStatus;
import com.example.demologin.repository.UserRepository;
import com.example.demologin.service.AuthenticationService;
import com.example.demologin.service.TokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        if (email == null) {
            response.sendRedirect("http://localhost:3000/login?error=missing_email");
            return;
        }

        try {
            // 🎉 Gọi lại service để tạo user mới nếu chưa có
            UserResponse userResponse = authenticationService.getUserResponse(email, name);

            // ✅ Redirect về frontend kèm token
            String redirectUrl = "http://localhost:3000/login?token=" + userResponse.getToken()
                    + "&refreshToken=" + userResponse.getRefreshToken();
            response.sendRedirect(redirectUrl);

        } catch (Exception e) {
            // ❌ Nếu có lỗi khi tạo user (như thiếu role MEMBER)
            response.sendRedirect("http://localhost:3000/login?error=" + e.getMessage().replace(" ", "_"));
        }
    }
}
