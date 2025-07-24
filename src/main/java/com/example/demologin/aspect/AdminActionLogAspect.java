package com.example.demologin.aspect;

import com.example.demologin.annotation.AdminAction;
import com.example.demologin.entity.AdminActionLog;
import com.example.demologin.entity.Role;
import com.example.demologin.entity.Permission;
import com.example.demologin.repository.RoleRepository;
import com.example.demologin.repository.PermissionRepository;
import com.example.demologin.service.AdminActionLogService;
import com.example.demologin.utils.AccountUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Objects;
import com.example.demologin.enums.AdminActionType;

@Aspect
@Component
public class AdminActionLogAspect {
    @Autowired private AdminActionLogService logService;
    @Autowired private HttpServletRequest request;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PermissionRepository permissionRepository;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Around("@annotation(com.example.demologin.annotation.AdminAction)")
    public Object logAdminAction(ProceedingJoinPoint jp) throws Throwable {
        MethodSignature signature = (MethodSignature) jp.getSignature();
        Method method = signature.getMethod();
        AdminAction adminAction = method.getAnnotation(AdminAction.class);
        Object[] args = jp.getArgs();
        String entity = adminAction.entity();
        String targetId = extractTargetId(args);
        Object oldEntityRaw = fetchOldEntity(entity, targetId);
        Object oldEntity = deepClone(oldEntityRaw); // clone trước khi thay đổi
        String reason = extractReason(args);
        if (Boolean.parseBoolean(adminAction.reasonRequired()) && (reason == null || reason.isBlank())) {
            throw new RuntimeException("Reason is required for this admin action!");
        }
        Long adminId = getCurrentAdminId();
        Object result = jp.proceed();
        Object newEntity = null;
        if (result != null && oldEntity != null && result.getClass().equals(oldEntity.getClass())) {
            newEntity = result;
        } else {
            newEntity = fetchOldEntity(entity, targetId); // fallback
        }
        String diff = compareChanges(oldEntity, newEntity);
        AdminActionType actionType;
        try {
            actionType = AdminActionType.valueOf(adminAction.action().toUpperCase());
        } catch (Exception e) {
            actionType = AdminActionType.OTHER;
        }
        AdminActionLog log = AdminActionLog.builder()
            .adminId(adminId)
            .targetType(entity)
            .targetId(targetId)
            .actionType(actionType)
            .reason(reason)
            .changeSummary(diff)
            .actionTime(LocalDateTime.now())
            .build();
        logService.save(log);
        return result;
    }
    private String extractTargetId(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof Long) {
                return arg.toString();
            }
        }
        return null;
    }
    private Object fetchOldEntity(String entity, String id) {
        if (id == null) return null;
        try {
            Long longId = Long.valueOf(id);
            if ("ROLE".equalsIgnoreCase(entity)) {
                return roleRepository.findById(longId).orElse(null);
            }
            if ("PERMISSION".equalsIgnoreCase(entity)) {
                return permissionRepository.findById(longId).orElse(null);
            }
        } catch (Exception ignored) {}
        return null;
    }
    private Long getCurrentAdminId() {
        try {
            return AccountUtils.getCurrentUser().getUserId();
        } catch (Exception e) {
            return 1L; // fallback nếu chưa login
        }
    }
    private String extractReason(Object[] args) {
        for (Object arg : args) {
            try {
                Field f = arg.getClass().getDeclaredField("adminAction");
                f.setAccessible(true);
                Object adminActionObj = f.get(arg);
                if (adminActionObj != null) {
                    Field reasonField = adminActionObj.getClass().getDeclaredField("reason");
                    reasonField.setAccessible(true);
                    return (String) reasonField.get(adminActionObj);
                }
            } catch (Exception ignored) {}
        }
        return null;
    }
    private String compareChanges(Object oldObj, Object newObj) {
        if (oldObj == null || newObj == null) return "Cannot compare";
        StringBuilder sb = new StringBuilder();
        for (Field field : oldObj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            if (fieldName.equalsIgnoreCase("id") || fieldName.equalsIgnoreCase("createdAt") || fieldName.equalsIgnoreCase("actionTime")) continue;
            try {
                Object o = field.get(oldObj), n = field.get(newObj);
                if (!Objects.equals(o, n)) {
                    sb.append(String.format("[Field: %s] '%s' → '%s'; ", fieldName, o, n));
                }
            } catch (Exception ignored) {}
        }
        return sb.length() > 0 ? sb.toString() : "No change";
    }
    private Object deepClone(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.readValue(objectMapper.writeValueAsBytes(obj), obj.getClass());
        } catch (Exception e) {
            return null;
        }
    }
} 