package com.example.demologin.initializer.components;

import com.example.demologin.entity.Permission;
import com.example.demologin.entity.Role;
import com.example.demologin.repository.PermissionRepository;
import com.example.demologin.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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

    // ===================== PERMISSION CODES =====================
    private static final String USER_MANAGE = "USER_MANAGE";  // mới thêm
    private static final String USER_TOKEN_MANAGEMENT = "USER_TOKEN_MANAGEMENT";
    private static final String TOKEN_INVALIDATE_OWN = "TOKEN_INVALIDATE_OWN";
    private static final String TOKEN_INVALIDATE_USER = "TOKEN_INVALIDATE_USER";
    private static final String TOKEN_VIEW_OWN = "TOKEN_VIEW_OWN";
    private static final String TOKEN_VIEW_USER = "TOKEN_VIEW_USER";

    private static final String ROLE_VIEW = "ROLE_VIEW";
    private static final String ROLE_CREATE = "ROLE_CREATE";
    private static final String ROLE_UPDATE = "ROLE_UPDATE";
    private static final String ROLE_DELETE = "ROLE_DELETE";
    private static final String ROLE_UPDATE_PERMISSIONS = "ROLE_UPDATE_PERMISSIONS";

    private static final String PERMISSION_VIEW = "PERMISSION_VIEW";
    private static final String PERMISSION_UPDATE = "PERMISSION_UPDATE";

    private static final String LOG_VIEW_ACTIVITY = "LOG_VIEW_ACTIVITY";
    private static final String ADMIN_ACTIVITY_LOG_EXPORT = "ADMIN_ACTIVITY_LOG_EXPORT";
    private static final String LOG_DELETE = "LOG_DELETE";

    private static final String USER_VIEW_OWN_LOGIN_HISTORY = "USER_VIEW_OWN_LOGIN_HISTORY";

    // Grade permissions
    private static final String GRADE_VIEW = "GRADE_VIEW";
    private static final String GRADE_CREATE = "GRADE_CREATE";
    private static final String GRADE_UPDATE = "GRADE_UPDATE";
    private static final String GRADE_DELETE = "GRADE_DELETE";

    // Level permissions
    private static final String LEVEL_VIEW = "LEVEL_VIEW";
    private static final String LEVEL_CREATE = "LEVEL_CREATE";
    private static final String LEVEL_UPDATE = "LEVEL_UPDATE";
    private static final String LEVEL_DELETE = "LEVEL_DELETE";

    // Exam Template permissions
    private static final String EXAM_TEMPLATE_VIEW = "EXAM_TEMPLATE_VIEW";
    private static final String EXAM_TEMPLATE_CREATE = "EXAM_TEMPLATE_CREATE";
    private static final String EXAM_TEMPLATE_UPDATE = "EXAM_TEMPLATE_UPDATE";
    private static final String EXAM_TEMPLATE_DELETE = "EXAM_TEMPLATE_DELETE";
    private static final String EXAM_TEMPLATE_MANAGE_QUESTIONS = "EXAM_TEMPLATE_MANAGE_QUESTIONS";
    private static final String EXAM_TEMPLATE_PUBLISH = "EXAM_TEMPLATE_PUBLISH";
    private static final String EXAM_TEMPLATE_APPROVE = "EXAM_TEMPLATE_APPROVE";

    // Exam Taking permissions (for students/members)
    private static final String EXAM_TAKE = "EXAM_TAKE";
    private static final String EXAM_VIEW_AVAILABLE = "EXAM_VIEW_AVAILABLE";
    private static final String EXAM_VIEW_RESULTS = "EXAM_VIEW_RESULTS";
    private static final String EXAM_VIEW_HISTORY = "EXAM_VIEW_HISTORY";

    private static final String QUESTION_VIEW   = "QUESTION_VIEW";
    private static final String QUESTION_CREATE = "QUESTION_CREATE";
    private static final String QUESTION_UPDATE = "QUESTION_UPDATE";
    private static final String QUESTION_DELETE = "QUESTION_DELETE";

    // Exam permissions (admin quản trị đề thi)
    private static final String EXAM_CREATE = "EXAM_CREATE";
    private static final String EXAM_UPDATE = "EXAM_UPDATE";
    private static final String EXAM_DELETE = "EXAM_DELETE";
    private static final String EXAM_QUESTION_ADD = "EXAM_QUESTION_ADD";
    private static final String EXAM_QUESTION_REMOVE = "EXAM_QUESTION_REMOVE";
    private static final String EXAM_PUBLISH = "EXAM_PUBLISH";
    private static final String EXAM_ARCHIVE = "EXAM_ARCHIVE";

    // Attempt/grade (admin)
    private static final String EXAM_ATTEMPT_MANAGE = "EXAM_ATTEMPT_MANAGE";
    private static final String EXAM_GRADE = "EXAM_GRADE";



    @Transactional
    public void initializePermissionsAndRoles() {
        log.info("🔑 Initializing system permissions and roles...");

        // Check if exam permissions exist, if not, add them
        if (permissionRepository.count() > 0) {
            log.info("ℹ️ Permissions exist, checking for new exam permissions...");

            // Check if exam permissions exist
            boolean examPermissionsExist = permissionRepository.findByCode(EXAM_VIEW_AVAILABLE).isPresent();

            if (!examPermissionsExist) {
                log.info("🔄 Adding missing exam permissions...");
                addExamPermissions();
                updateMemberRoleWithExamPermissions();
                log.info("✅ Added exam permissions to existing system");
            } else {
                log.info("ℹ️ Exam permissions already exist, skipping initialization");
            }
            return;
        }

        createPermissions();
        createRoles();

        log.info("✅ Successfully initialized {} permissions and {} roles",
                permissionRepository.count(), roleRepository.count());
    }

    private void createPermissions() {
        log.debug("📋 Creating system permissions...");

        List<Permission> permissions = Arrays.asList(
                new Permission(USER_MANAGE, "Quản lý user (Admin)"),
                new Permission(USER_TOKEN_MANAGEMENT, "Quản lý token của user"),
                new Permission(TOKEN_INVALIDATE_OWN, "Hủy token của bản thân"),
                new Permission(TOKEN_INVALIDATE_USER, "Hủy token của user cụ thể"),
                new Permission(TOKEN_VIEW_OWN, "Xem token version của bản thân"),
                new Permission(TOKEN_VIEW_USER, "Xem token version của user cụ thể"),
                new Permission(ROLE_VIEW, "Xem vai trò"),
                new Permission(ROLE_CREATE, "Tạo vai trò"),
                new Permission(ROLE_UPDATE, "Cập nhật vai trò"),
                new Permission(ROLE_DELETE, "Xóa vai trò"),
                new Permission(ROLE_UPDATE_PERMISSIONS, "Gán quyền cho vai trò"),
                new Permission(PERMISSION_VIEW, "Xem quyền"),
                new Permission(PERMISSION_UPDATE, "Cập nhật quyền"),
                new Permission(LOG_VIEW_ACTIVITY, "Xem user activity logs"),
                new Permission(ADMIN_ACTIVITY_LOG_EXPORT, "Export user activity logs"),
                new Permission(LOG_DELETE, "Xóa user activity logs"),
                new Permission(USER_VIEW_OWN_LOGIN_HISTORY, "Xem lịch sử đăng nhập của bản thân"),
                
                // Grade permissions
                new Permission(GRADE_VIEW, "Xem danh sách grade"),
                new Permission(GRADE_CREATE, "Tạo grade mới"),
                new Permission(GRADE_UPDATE, "Cập nhật grade"),
                new Permission(GRADE_DELETE, "Xóa grade"),
                
                // Level permissions
                new Permission(LEVEL_VIEW, "Xem danh sách level"),
                new Permission(LEVEL_CREATE, "Tạo level mới"),
                new Permission(LEVEL_UPDATE, "Cập nhật level"),
                new Permission(LEVEL_DELETE, "Xóa level"),
                
                // Exam Template permissions
                new Permission(EXAM_TEMPLATE_VIEW, "Xem danh sách exam template"),
                new Permission(EXAM_TEMPLATE_CREATE, "Tạo exam template mới"),
                new Permission(EXAM_TEMPLATE_UPDATE, "Cập nhật exam template"),
                new Permission(EXAM_TEMPLATE_DELETE, "Xóa exam template"),
                new Permission(EXAM_TEMPLATE_MANAGE_QUESTIONS, "Quản lý câu hỏi trong exam template"),
                new Permission(EXAM_TEMPLATE_PUBLISH, "Publish exam template"),
                new Permission(EXAM_TEMPLATE_APPROVE, "Approve exam template"),

                // Exam Taking permissions
                new Permission(EXAM_TAKE, "Làm bài thi"),
                new Permission(EXAM_VIEW_AVAILABLE, "Xem danh sách bài thi có sẵn"),
                new Permission(EXAM_VIEW_RESULTS, "Xem kết quả bài thi"),
                new Permission(EXAM_VIEW_HISTORY, "Xem lịch sử làm bài"),

                new Permission(QUESTION_VIEW,   "Xem câu hỏi"),
                new Permission(QUESTION_CREATE, "Tạo câu hỏi"),
                new Permission(QUESTION_UPDATE, "Cập nhật câu hỏi"),
                new Permission(QUESTION_DELETE, "Xóa câu hỏi"),

                new Permission(EXAM_CREATE, "Tạo bài thi"),
                new Permission(EXAM_UPDATE, "Cập nhật bài thi"),
                new Permission(EXAM_DELETE, "Xóa bài thi"),
                new Permission(EXAM_QUESTION_ADD, "Thêm câu hỏi vào bài thi"),
                new Permission(EXAM_QUESTION_REMOVE, "Gỡ câu hỏi khỏi bài thi"),
                new Permission(EXAM_PUBLISH, "Publish bài thi"),
                new Permission(EXAM_ARCHIVE, "Archive bài thi"),
                new Permission(EXAM_ATTEMPT_MANAGE, "Quản lý bài làm (attempt)"),
                new Permission(EXAM_GRADE, "Chấm bài thi")


        );

        permissionRepository.saveAll(permissions);

        log.debug("✅ Created {} permissions", permissionRepository.count());
    }

    private void createRoles() {
        log.debug("👑 Creating system roles...");

        // Tạo map {code -> Permission}
        Map<String, Permission> permMap = permissionRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Permission::getCode, p -> p));

        // Admin: full quyền
        Set<Permission> adminPerms = new HashSet<>(permMap.values());

        // Member: quyền giới hạn + exam taking permissions
        Set<Permission> memberPerms = Set.of(
                permMap.get(USER_TOKEN_MANAGEMENT),
                permMap.get(TOKEN_INVALIDATE_OWN),
                permMap.get(TOKEN_VIEW_OWN),
                permMap.get(USER_VIEW_OWN_LOGIN_HISTORY),
                permMap.get(EXAM_TAKE),
                permMap.get(EXAM_VIEW_AVAILABLE),
                permMap.get(EXAM_VIEW_RESULTS),
                permMap.get(EXAM_VIEW_HISTORY)
        );

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

    private void addExamPermissions() {
        log.debug("📋 Adding missing exam permissions...");

        List<Permission> examPermissions = Arrays.asList(
                new Permission(EXAM_TAKE, "Làm bài thi"),
                new Permission(EXAM_VIEW_AVAILABLE, "Xem danh sách bài thi có sẵn"),
                new Permission(EXAM_VIEW_RESULTS, "Xem kết quả bài thi"),
                new Permission(EXAM_VIEW_HISTORY, "Xem lịch sử làm bài")
        );

        permissionRepository.saveAll(examPermissions);
        log.debug("✅ Added {} exam permissions", examPermissions.size());
    }

    private void updateMemberRoleWithExamPermissions() {
        log.debug("👑 Updating MEMBER role with exam permissions...");

        Optional<Role> memberRoleOpt = roleRepository.findByName("MEMBER");
        if (memberRoleOpt.isPresent()) {
            Role memberRole = memberRoleOpt.get();

            // Get the new exam permissions
            List<Permission> examPermissions = Arrays.asList(
                    permissionRepository.findByCode(EXAM_TAKE).orElse(null),
                    permissionRepository.findByCode(EXAM_VIEW_AVAILABLE).orElse(null),
                    permissionRepository.findByCode(EXAM_VIEW_RESULTS).orElse(null),
                    permissionRepository.findByCode(EXAM_VIEW_HISTORY).orElse(null)
            );

            // Filter out null permissions
            examPermissions = examPermissions.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            // Add exam permissions to existing permissions
            Set<Permission> currentPermissions = new HashSet<>(memberRole.getPermissions());
            currentPermissions.addAll(examPermissions);

            memberRole.setPermissions(currentPermissions);
            roleRepository.save(memberRole);

            log.debug("✅ Updated MEMBER role with {} exam permissions", examPermissions.size());
        } else {
            log.warn("⚠️ MEMBER role not found, cannot update with exam permissions");
        }
    }
}
