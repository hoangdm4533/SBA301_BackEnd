package com.example.demologin.config;

import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.dto.response.UserResponse;
import com.example.demologin.entity.User;
import com.example.demologin.repository.UserRepository;
import com.example.demologin.service.AuthenticationService;
import com.example.demologin.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        response.setContentType("application/json");
        if (email == null) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            ResponseObject resp = new ResponseObject(HttpStatus.BAD_REQUEST.value(), "missing_email", null);
            new ObjectMapper().writeValue(response.getWriter(), resp);
            return;
        }
        try {
            UserResponse userResponse = authenticationService.getUserResponse(email, name);
            response.setStatus(HttpStatus.OK.value());
            ResponseObject resp = new ResponseObject(HttpStatus.OK.value(), "OAuth2 login successful", userResponse);
            new ObjectMapper().writeValue(response.getWriter(), resp);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            ResponseObject resp = new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
            new ObjectMapper().writeValue(response.getWriter(), resp);
        }
    }
}
