package com.example.demologin.serviceImpl;

import com.example.demologin.dto.request.user.AdminUpdateUserRequest;
import com.example.demologin.dto.request.user.CreateUserRequest;
import com.example.demologin.dto.request.user.UpdateUserRequest;
import com.example.demologin.dto.response.MemberResponse;
import com.example.demologin.entity.Role;
import com.example.demologin.entity.User;
import com.example.demologin.enums.UserStatus;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
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



        return userMapper.toUserResponse(userRepository.save(u));
    }

    @Override
    public Object updateSelf(Long currentUserId, UpdateUserRequest req) {
        User u = userRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // --- CHECK UNIQUE USERNAME ---
        if (req.getUsername() != null && !req.getUsername().isBlank()) {
            if (userRepository.existsByUsernameAndUserIdNot(req.getUsername(), currentUserId)) {
                throw new IllegalArgumentException("Username already in use by another user");
            }
            u.setUsername(req.getUsername());
        }

        if (req.getEmail() != null &&
                userRepository.existsByEmailAndUserIdNot(req.getEmail(), currentUserId)) {
            throw new IllegalArgumentException("Email already in use by another user");
        }

        if (req.getFullName() != null) u.setFullName(req.getFullName());
        if (req.getEmail() != null)    u.setEmail(req.getEmail());
        if (req.getGender() != null)   u.setGender(req.getGender());

        if (req.getNewPassword() != null && !req.getNewPassword().isBlank()) {
            u.setPassword(passwordEncoder.encode(req.getNewPassword()));
            u.incrementTokenVersion();
        }

        return userMapper.toUserResponse(userRepository.save(u));
    }

    @Override
    public Object updateAdmin(Long id, AdminUpdateUserRequest req) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // --- CHECK UNIQUE USERNAME ---
        if (req.getUsername() != null && !req.getUsername().isBlank()) {
            if (userRepository.existsByUsernameAndUserIdNot(req.getUsername(), id)) {
                throw new IllegalArgumentException("Username already in use by another user");
            }
            u.setUsername(req.getUsername());
        }

        if (req.getEmail() != null &&
                userRepository.existsByEmailAndUserIdNot(req.getEmail(), id)) {
            throw new IllegalArgumentException("Email already in use by another user");
        }

        if (req.getFullName() != null) u.setFullName(req.getFullName());
        if (req.getEmail() != null)    u.setEmail(req.getEmail());
        if (req.getGender() != null)   u.setGender(req.getGender());

        if (req.getNewPassword() != null && !req.getNewPassword().isBlank()) {
            u.setPassword(passwordEncoder.encode(req.getNewPassword()));
            u.incrementTokenVersion();
        }

        // --- ADMIN-ONLY FIELDS ---
        if (req.getStatus() != null) u.setStatus(req.getStatus());
        if (req.getLocked() != null) u.setLocked(req.getLocked());
        if (req.getVerify() != null) u.setVerify(req.getVerify());

        if (req.getRoles() != null) {
            List<Role> found = roleRepository.findAllByNameIn(req.getRoles());
            Set<String> foundNames = found.stream().map(Role::getName).collect(Collectors.toSet());
            Set<String> requested = new HashSet<>(req.getRoles());
            requested.removeAll(foundNames);
            if (!requested.isEmpty()) {
                throw new IllegalArgumentException("Unknown roles: " + String.join(", ", requested));
            }
            u.setRoles(new HashSet<>(found));
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
