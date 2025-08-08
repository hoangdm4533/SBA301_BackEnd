package com.example.demologin.serviceImpl;

import com.example.demologin.dto.request.user.UpdateUserRequest;
import com.example.demologin.dto.response.MemberResponse;
import com.example.demologin.entity.User;
import com.example.demologin.exception.exceptions.NotFoundException;
import com.example.demologin.repository.UserRepository;
import com.example.demologin.mapper.UserMapper;
import com.example.demologin.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
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
