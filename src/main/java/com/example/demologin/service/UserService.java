package com.example.demologin.service;

import com.example.demologin.dto.request.user.AdminUpdateUserRequest;
import com.example.demologin.dto.request.user.CreateUserRequest;
import com.example.demologin.dto.request.user.UpdateUserRequest;
import com.example.demologin.dto.response.MemberResponse;
import org.springframework.data.domain.Page;

public interface UserService {
    Page<MemberResponse> getAllUsers(int page, int size);
    MemberResponse getById(Long id);
    MemberResponse create(CreateUserRequest req);
    Object updateSelf(Long currentUserId, UpdateUserRequest req);
    Object updateAdmin(Long id, AdminUpdateUserRequest req);
    void delete(Long id);
}
