package com.example.demologin.serviceImpl;

import com.example.demologin.dto.request.user.CreateUserRequest;
import com.example.demologin.dto.request.user.UpdateUserRequest;
import com.example.demologin.dto.response.MemberResponse;
import com.example.demologin.entity.ClassEntity;
import com.example.demologin.entity.Role;
import com.example.demologin.entity.User;
import com.example.demologin.enums.UserStatus;
import com.example.demologin.repository.ClassEntityRepository;
import com.example.demologin.repository.RoleRepository;
import com.example.demologin.repository.UserRepository;
import com.example.demologin.mapper.UserMapper;
import com.example.demologin.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;


@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final ClassEntityRepository classEntityRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public Page<MemberResponse> getAllUsers(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findAll(pageable);
        return users.map(userMapper::toUserResponse);
    }

    @Override
    public MemberResponse getById(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return userMapper.toUserResponse(u);
    }

    @Override
    public MemberResponse create(CreateUserRequest req) {
        if (userRepository.existsByUsername(req.getUsername()))
            throw new IllegalArgumentException("Username already exists");
        if (userRepository.existsByEmail(req.getEmail()))
            throw new IllegalArgumentException("Email already exists");

        User u = new User();
        u.setUsername(req.getUsername());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setFullName(req.getFullName());
        u.setEmail(req.getEmail());
        u.setGender(req.getGender());
        u.setStatus(UserStatus.ACTIVE);
        u.setVerify(false);
        u.setLocked(false);

        // Roles
        Set<Role> roles = new HashSet<>();
        for (String name : req.getRoles()) {
            Role r = roleRepository.findByName(name)
                    .orElseThrow(() -> new EntityNotFoundException("Role not found: " + name));
            roles.add(r);
        }
        u.setRoles(roles);

        // Class (optional)
        if (req.getClassId() != null) {
            ClassEntity c = classEntityRepository.findById(req.getClassId())
                    .orElseThrow(() -> new EntityNotFoundException("Class not found"));
            u.setClassEntity(c);
        }

        return userMapper.toUserResponse(userRepository.save(u));
    }

    @Override
    public MemberResponse update(Long id, UpdateUserRequest req) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (req.getEmail() != null &&
                userRepository.existsByEmailAndUserIdNot(req.getEmail(), id))
            throw new IllegalArgumentException("Email already in use by another user");

        if (req.getFullName() != null) u.setFullName(req.getFullName());
        if (req.getEmail() != null) u.setEmail(req.getEmail());
        if (req.getGender() != null) u.setGender(req.getGender());
        if (req.getStatus() != null) u.setStatus(req.getStatus());
        if (req.getLocked() != null) u.setLocked(req.getLocked());
        if (req.getVerify() != null) u.setVerify(req.getVerify());

        if (req.getNewPassword() != null && !req.getNewPassword().isBlank()) {
            u.setPassword(passwordEncoder.encode(req.getNewPassword()));
            u.incrementTokenVersion(); // buộc logout các phiên cũ nếu bạn dùng token version
        }

        if (req.getRoles() != null) {
            Set<Role> roles = new HashSet<>();
            for (String name : req.getRoles()) {
                Role r = roleRepository.findByName(name)
                        .orElseThrow(() -> new EntityNotFoundException("Role not found: " + name));
                roles.add(r);
            }
            u.setRoles(roles);
        }

        if (req.getClassId() != null) {
            ClassEntity c = classEntityRepository.findById(req.getClassId())
                    .orElseThrow(() -> new EntityNotFoundException("Class not found"));
            u.setClassEntity(c);
        }

        return userMapper.toUserResponse(userRepository.save(u));
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id))
            throw new EntityNotFoundException("User not found");
        userRepository.deleteById(id);
    }
} 
