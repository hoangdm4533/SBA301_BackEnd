package com.example.demologin.service;

import com.example.demologin.entity.User;

public interface TokenService {
    String generateTokenForUser(User user);
    User getUserByToken(String token);
}
