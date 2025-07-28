package com.example.demologin.serviceImpl;

import com.example.demologin.annotation.AdminActionLog;
import com.example.demologin.dto.request.UpdateUserRequest;
import com.example.demologin.dto.response.MemberResponse;
import com.example.demologin.entity.User;
import com.example.demologin.enums.AdminActionType;
import com.example.demologin.exception.exceptions.NotFoundException;
import com.example.demologin.repository.UserRepository;
import com.example.demologin.mapper.UserMapper;
import com.example.demologin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired private UserRepository userRepository;
    @Autowired private UserMapper userMapper;

    @AdminActionLog(targetType = "USER", actionType = AdminActionType.UPDATE)
    @Transactional
    @Override
    public MemberResponse updateUser(UpdateUserRequest req) {
        User user = userRepository.findById(req.getUserId()).orElseThrow(() -> new NotFoundException("User with id " + req.getUserId() + " not found"));
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }
} 