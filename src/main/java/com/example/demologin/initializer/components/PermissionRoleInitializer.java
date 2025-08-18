package com.example.demologin.initializer.components;

import com.example.demologin.entity.Permission;
import com.example.demologin.entity.Role;
import com.example.demologin.repository.PermissionRepository;
import com.example.demologin.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * Permission and Role Initializer
 * 
 * Responsible for creating all system permissions and roles.
 * This must run before user initialization since users depend on roles.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionRoleInitializer {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public void initializePermissionsAndRoles() {
        log.info("🔑 Initializing system permissions and roles...");
        
        if (permissionRepository.count() > 0) {
            log.info("ℹ️ Permissions already exist, skipping initialization");
            return;
        }

        createPermissions();
        createRoles();
        
        log.info("✅ Successfully initialized {} permissions and {} roles", 
                permissionRepository.count(), roleRepository.count());
    }

    private void createPermissions() {
        log.debug("📋 Creating system permissions...");
        
        // Session Management Permissions  
        permissionRepository.save(new Permission("USER_TOKEN_MANAGEMENT", "Quản lý token của user"));
        permissionRepository.save(new Permission("ADMIN_USER_MANAGEMENT", "Quản lý user (Admin)"));
        
        // Token Management Permissions
        permissionRepository.save(new Permission("TOKEN_INVALIDATE_OWN", "Hủy token của bản thân"));
        permissionRepository.save(new Permission("TOKEN_INVALIDATE_USER", "Hủy token của user cụ thể"));
        permissionRepository.save(new Permission("TOKEN_VIEW_OWN", "Xem token version của bản thân"));
        permissionRepository.save(new Permission("TOKEN_VIEW_USER", "Xem token version của user cụ thể"));
        
        // Role & Permission Management
        permissionRepository.save(new Permission("ROLE_VIEW", "Xem vai trò"));
        permissionRepository.save(new Permission("ROLE_CREATE", "Tạo vai trò"));
        permissionRepository.save(new Permission("ROLE_UPDATE", "Cập nhật vai trò"));
        permissionRepository.save(new Permission("ROLE_DELETE", "Xóa vai trò"));
        permissionRepository.save(new Permission("ROLE_UPDATE_PERMISSIONS", "Gán quyền cho vai trò"));
        permissionRepository.save(new Permission("PERMISSION_VIEW", "Xem quyền"));
        permissionRepository.save(new Permission("PERMISSION_UPDATE", "Cập nhật quyền"));
        
        // User Activity Log Permissions
        permissionRepository.save(new Permission("LOG_VIEW_ACTIVITY", "Xem user activity logs"));
        permissionRepository.save(new Permission("ADMIN_ACTIVITY_LOG_EXPORT", "Export user activity logs"));
        permissionRepository.save(new Permission("LOG_DELETE", "Xóa user activity logs"));
        
        // User Personal Data Permissions
        permissionRepository.save(new Permission("USER_VIEW_OWN_LOGIN_HISTORY", "Xem lịch sử đăng nhập của bản thân"));
        
        log.debug("✅ Created {} permissions", permissionRepository.count());
    }

    private void createRoles() {
        log.debug("👑 Creating system roles...");
        
        // Get all permissions for role assignment
        Permission p1 = permissionRepository.findByCode("USER_TOKEN_MANAGEMENT").orElseThrow();
        Permission p2 = permissionRepository.findByCode("ADMIN_USER_MANAGEMENT").orElseThrow();
        Permission p3 = permissionRepository.findByCode("TOKEN_INVALIDATE_OWN").orElseThrow();
        Permission p4 = permissionRepository.findByCode("TOKEN_INVALIDATE_USER").orElseThrow();
        Permission p5 = permissionRepository.findByCode("TOKEN_VIEW_OWN").orElseThrow();
        Permission p6 = permissionRepository.findByCode("TOKEN_VIEW_USER").orElseThrow();
        Permission p7 = permissionRepository.findByCode("ROLE_VIEW").orElseThrow();
        Permission p8 = permissionRepository.findByCode("ROLE_CREATE").orElseThrow();
        Permission p9 = permissionRepository.findByCode("ROLE_UPDATE").orElseThrow();
        Permission p10 = permissionRepository.findByCode("ROLE_DELETE").orElseThrow();
        Permission p11 = permissionRepository.findByCode("ROLE_UPDATE_PERMISSIONS").orElseThrow();
        Permission p12 = permissionRepository.findByCode("PERMISSION_VIEW").orElseThrow();
        Permission p13 = permissionRepository.findByCode("PERMISSION_UPDATE").orElseThrow();
        Permission p14 = permissionRepository.findByCode("LOG_VIEW_ACTIVITY").orElseThrow();
        Permission p15 = permissionRepository.findByCode("ADMIN_ACTIVITY_LOG_EXPORT").orElseThrow();
        Permission p16 = permissionRepository.findByCode("LOG_DELETE").orElseThrow();
        Permission p17 = permissionRepository.findByCode("USER_VIEW_OWN_LOGIN_HISTORY").orElseThrow();

        // Admin permissions (full access)
        Set<Permission> adminPerms = new HashSet<>();
        adminPerms.add(p1); adminPerms.add(p2); adminPerms.add(p3); adminPerms.add(p4);
        adminPerms.add(p5); adminPerms.add(p6); adminPerms.add(p7); adminPerms.add(p8);
        adminPerms.add(p9); adminPerms.add(p10); adminPerms.add(p11); adminPerms.add(p12);
        adminPerms.add(p13); adminPerms.add(p14); adminPerms.add(p15); adminPerms.add(p16);
        adminPerms.add(p17);
        
        // Member permissions (limited access)
        Set<Permission> memberPerms = new HashSet<>();
        memberPerms.add(p1); // USER_TOKEN_MANAGEMENT
        memberPerms.add(p3); // TOKEN_INVALIDATE_OWN  
        memberPerms.add(p5); // TOKEN_VIEW_OWN
        memberPerms.add(p17); // USER_VIEW_OWN_LOGIN_HISTORY

        roleRepository.save(Role.builder()
                .name("ADMIN")
                .permissions(adminPerms)
                .build());

        roleRepository.save(Role.builder()
                .name("MEMBER")
                .permissions(memberPerms)
                .build());

        
        log.debug("✅ Created {} roles", roleRepository.count());
    }
}
