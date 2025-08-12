package com.example.demologin.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class IpUtilsWrapper {
    public String getClientIpAddress() {
        return IpUtils.getClientIpAddress();
    }

    public String getUserAgent() {
        return IpUtils.getUserAgent();
    }

    public String getClientIpAddress(HttpServletRequest request) {
        return IpUtils.getClientIpAddress(request);
    }

    public String getUserAgent(HttpServletRequest request) {
        return IpUtils.getUserAgent(request);
    }
}