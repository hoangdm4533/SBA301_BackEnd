package com.example.demologin.serviceImpl;

import com.example.demologin.entity.User;
import com.example.demologin.exception.exceptions.NotFoundException;
import com.example.demologin.repository.UserRepository;
import com.example.demologin.service.RolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
public class RolePermissionServiceImpl implements RolePermissionService {
    @Autowired private UserRepository userRepo;
    @Override
    public Set<String> getUserPermissionCodes(Long userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        return user.getPermissionCodes();
    }
} 