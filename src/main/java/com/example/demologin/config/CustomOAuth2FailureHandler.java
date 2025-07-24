package com.example.demologin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demologin.dto.response.ResponseObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomOAuth2FailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        org.springframework.security.core.AuthenticationException exception)
            throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        ResponseObject resp = new ResponseObject(HttpStatus.UNAUTHORIZED.value(), exception.getMessage(), null);
        new ObjectMapper().writeValue(response.getWriter(), resp);
    }
}
