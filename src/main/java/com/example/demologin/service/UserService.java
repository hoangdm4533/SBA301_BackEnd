package com.example.demologin.service;

import com.example.demologin.dto.request.user.UpdateUserRequest;
import com.example.demologin.dto.response.MemberResponse;

public interface UserService {
    MemberResponse updateUser(UpdateUserRequest req);
} 
