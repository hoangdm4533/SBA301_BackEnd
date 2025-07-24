package com.example.demologin.service;

import com.example.demologin.annotation.AdminActionLog;
import com.example.demologin.dto.request.UpdateUserRequest;
import com.example.demologin.dto.response.MemberResponse;
import com.example.demologin.entity.User;
import com.example.demologin.enums.AdminActionType;
import com.example.demologin.repository.UserRepository;
import com.example.demologin.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {
    MemberResponse updateUser(UpdateUserRequest req);
} 