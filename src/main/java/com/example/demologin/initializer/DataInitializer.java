package com.example.demologin.initializer;

import com.example.demologin.entity.Permission;
import com.example.demologin.entity.User;
import com.example.demologin.enums.Gender;
import com.example.demologin.enums.UserStatus;
import com.example.demologin.repository.PermissionRepository;
import com.example.demologin.repository.RoleRepository;
import com.example.demologin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public void run(String... args) {
        System.out.println("DataInitializer is running...");

        if (userRepository.count() == 0) {
            createDefaultPermissionsAndRoles();
            createDefaultUsers();
        }
    }

    private void createDefaultPermissionsAndRoles() {
        Permission p2 = permissionRepository.save(Permission.builder().code("USER_UPDATE").name("Cập nhật người dùng").build());
        Permission p3 = permissionRepository.save(Permission.builder().code("ROLE_UPDATE").name("Cập nhật vai trò").build());
        Permission p4 = permissionRepository.save(Permission.builder().code("PERMISSION_VIEW").name("Xem quyền").build());
        Permission p6 = permissionRepository.save(Permission.builder().code("PERMISSION_UPDATE").name("Sửa quyền").build());
        Permission p8 = permissionRepository.save(Permission.builder().code("ROLE_VIEW").name("Xem vai trò").build());
        Permission p9 = permissionRepository.save(Permission.builder().code("ROLE_CREATE").name("Tạo vai trò").build());
        Permission p10 = permissionRepository.save(Permission.builder().code("ROLE_DELETE").name("Xóa vai trò").build());
        Permission p11 = permissionRepository.save(Permission.builder().code("ROLE_UPDATE_PERMISSIONS").name("Gán quyền cho vai trò").build());

        Set<Permission> adminPerms = new HashSet<>();
        adminPerms.add(p2); adminPerms.add(p3);
        adminPerms.add(p4); adminPerms.add(p6);
        adminPerms.add(p8); adminPerms.add(p9); adminPerms.add(p10); adminPerms.add(p11);
        Set<Permission> memberPerms = new HashSet<>();
        // memberPerms.add(p1); // Không còn USER_VIEW

        roleRepository.save(com.example.demologin.entity.Role.builder().name("ADMIN").permissions(adminPerms).build());
        roleRepository.save(com.example.demologin.entity.Role.builder().name("MEMBER").permissions(memberPerms).build());
    }

    private void createUser(String username, String rawPassword, String roleName) {
        if (userRepository.findByUsername(username).isPresent()) {
            return;
        }
        com.example.demologin.entity.Role role = roleRepository.findByName(roleName).orElseThrow();
        Set<com.example.demologin.entity.Role> roles = new HashSet<>();
        roles.add(role);
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .roles(roles)
                .fullName(username + " Fullname")
                .email(username + "@example.com")
                .phone("0123456789")
                .address("123 Main Street")
                .identityCard("123456789")
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .status(UserStatus.ACTIVE)
                .gender(Gender.OTHER)
                .tokenVersion(0)
                .locked(false)
                .build();
        userRepository.save(user);
        System.out.println("Created user with role: " + roleName);
    }

    private void createDefaultUsers() {
        createUser("admin", "admin123", "ADMIN");
        createUser("member", "member123", "MEMBER");
    }
}
