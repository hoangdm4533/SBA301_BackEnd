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
        // Session Management Permissions  
        Permission p1 = permissionRepository.save(new Permission("USER_TOKEN_MANAGEMENT", "Quản lý token của user"));
        Permission p2 = permissionRepository.save(new Permission("ADMIN_USER_MANAGEMENT", "Quản lý user (Admin)"));
        
        // Token Management Permissions
        Permission p3 = permissionRepository.save(new Permission("TOKEN_INVALIDATE_OWN", "Hủy token của bản thân"));
        Permission p4 = permissionRepository.save(new Permission("TOKEN_INVALIDATE_ANY", "Hủy token của bất kỳ user nào"));
        Permission p5 = permissionRepository.save(new Permission("TOKEN_VIEW_OWN", "Xem token version của bản thân"));
        Permission p6 = permissionRepository.save(new Permission("TOKEN_VIEW_ANY", "Xem token version của bất kỳ user nào"));
        Permission p7 = permissionRepository.save(new Permission("TOKEN_RESET", "Reset token version"));
        Permission p8 = permissionRepository.save(new Permission("TOKEN_VALIDATE", "Validate token version"));
        
        // User Action Log Permissions
        Permission p9 = permissionRepository.save(new Permission("LOG_VIEW_ALL", "Xem tất cả action logs"));
        Permission p10 = permissionRepository.save(new Permission("LOG_VIEW_USER", "Xem action logs của user cụ thể"));
        Permission p11 = permissionRepository.save(new Permission("LOG_SEARCH", "Tìm kiếm action logs"));
        Permission p12 = permissionRepository.save(new Permission("LOG_DELETE", "Xóa action logs"));
        
        // Role & Permission Management
        Permission p13 = permissionRepository.save(new Permission("ROLE_VIEW", "Xem vai trò"));
        Permission p14 = permissionRepository.save(new Permission("ROLE_CREATE", "Tạo vai trò"));
        Permission p15 = permissionRepository.save(new Permission("ROLE_UPDATE", "Cập nhật vai trò"));
        Permission p16 = permissionRepository.save(new Permission("ROLE_DELETE", "Xóa vai trò"));
        Permission p17 = permissionRepository.save(new Permission("ROLE_UPDATE_PERMISSIONS", "Gán quyền cho vai trò"));
        Permission p18 = permissionRepository.save(new Permission("PERMISSION_VIEW", "Xem quyền"));
        Permission p19 = permissionRepository.save(new Permission("PERMISSION_UPDATE", "Cập nhật quyền"));
        
        // Admin Action Log
        Permission p20 = permissionRepository.save(new Permission("ADMIN_ACTION_LOG_VIEW", "Xem admin action log"));
        
        // User Activity Log Permissions
        Permission p21 = permissionRepository.save(new Permission("LOG_VIEW_ACTIVITY", "Xem user activity logs"));
        Permission p22 = permissionRepository.save(new Permission("LOG_SEARCH_ACTIVITY", "Tìm kiếm user activity logs"));
        Permission p23 = permissionRepository.save(new Permission("LOG_VIEW_USER_ACTIVITY", "Xem activity logs của user cụ thể"));
        Permission p24 = permissionRepository.save(new Permission("LOG_EXPORT_ACTIVITY", "Export user activity logs"));
        Permission p25 = permissionRepository.save(new Permission("LOG_VIEW_STATS", "Xem thống kê activity"));
        Permission p26 = permissionRepository.save(new Permission("LOG_DELETE_ACTIVITY", "Xóa user activity logs"));

        // Security Management Permissions
        Permission p27 = permissionRepository.save(new Permission("ADMIN_SECURITY_MANAGEMENT", "Quản lý bảo mật tài khoản (unlock, xem login attempts)"));

        // Admin permissions (full access)
        Set<Permission> adminPerms = new HashSet<>();
        adminPerms.add(p1); adminPerms.add(p2); adminPerms.add(p3); adminPerms.add(p4);
        adminPerms.add(p5); adminPerms.add(p6); adminPerms.add(p7); adminPerms.add(p8);
        adminPerms.add(p9); adminPerms.add(p10); adminPerms.add(p11); adminPerms.add(p12);
        adminPerms.add(p13); adminPerms.add(p14); adminPerms.add(p15); adminPerms.add(p16);
        adminPerms.add(p17); adminPerms.add(p18); adminPerms.add(p19); adminPerms.add(p20);
        adminPerms.add(p21); adminPerms.add(p22); adminPerms.add(p23); adminPerms.add(p24);
        adminPerms.add(p25); adminPerms.add(p26); adminPerms.add(p27);
        
        // Member permissions (limited access)
        Set<Permission> memberPerms = new HashSet<>();
        memberPerms.add(p1); // USER_TOKEN_MANAGEMENT
        memberPerms.add(p3); // TOKEN_INVALIDATE_OWN  
        memberPerms.add(p5); // TOKEN_VIEW_OWN
        memberPerms.add(p8); // TOKEN_VALIDATE

        roleRepository.save(new com.example.demologin.entity.Role("ADMIN", adminPerms));
        roleRepository.save(new com.example.demologin.entity.Role("MEMBER", memberPerms));
    }

    private void createUser(String username, String rawPassword, String roleName) {
        if (userRepository.findByUsername(username).isPresent()) {
            return;
        }
        com.example.demologin.entity.Role role = roleRepository.findByName(roleName).orElseThrow();
        Set<com.example.demologin.entity.Role> roles = new HashSet<>();
        roles.add(role);
        
        User user = new User(
                username,
                passwordEncoder.encode(rawPassword),
                username + " Fullname",
                username + "@example.com",
                "0123456789",
                "123 Main Street"
        );
        
        // Set additional properties
        user.setRoles(roles);
        user.setIdentityCard("123456789");
        user.setDateOfBirth(LocalDate.of(1995, 1, 1));
        user.setStatus(UserStatus.ACTIVE);
        user.setGender(Gender.OTHER);
        user.setTokenVersion(0);
        user.setVerify(true);
        user.setLocked(false);
        
        userRepository.save(user);
        System.out.println("Created user with role: " + roleName);
    }

    private void createDefaultUsers() {
        createUser("admin", "admin123", "ADMIN");
        createUser("member", "member123", "MEMBER");
    }
}
