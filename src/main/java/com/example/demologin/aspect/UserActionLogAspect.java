package com.example.demologin.aspect;

import com.example.demologin.annotation.UserAction;
import com.example.demologin.entity.*;
import com.example.demologin.repository.*;
import com.example.demologin.service.UserActionLogService;
import com.example.demologin.utils.AccountUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Objects;

@Aspect
@Component
public class UserActionLogAspect {
    
    private static final Logger log = LoggerFactory.getLogger(UserActionLogAspect.class);
    
    private final UserActionLogService userActionLogService;
    private final HttpServletRequest request;
    private final ApplicationContext applicationContext;
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    public UserActionLogAspect(UserActionLogService userActionLogService, 
                              HttpServletRequest request, 
                              ApplicationContext applicationContext) {
        this.userActionLogService = userActionLogService;
        this.request = request;
        this.applicationContext = applicationContext;
    }
    
    @Around("@annotation(com.example.demologin.annotation.UserAction)")
    public Object logUserAction(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        UserAction userAction = method.getAnnotation(UserAction.class);
        Object[] args = joinPoint.getArgs();
        
        // Lấy thông tin user hiện tại
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            log.warn("Cannot log action: No current user found");
            return joinPoint.proceed();
        }
        
        // Tự động xác định targetType từ method name hoặc parameter
        String targetType = determineTargetType(userAction, method, args);
        
        // Tự động lấy targetId từ parameters
        String targetId = extractTargetId(args);
        
        // Lấy entity cũ để so sánh thay đổi
        Object oldEntity = fetchOldEntity(targetType, targetId);
        Object clonedOldEntity = deepClone(oldEntity);
        
        // Kiểm tra reason nếu được yêu cầu
        String reason = extractReason(args);
        if (userAction.requiresReason()) {
            if (reason == null || reason.trim().isEmpty()) {
                // Tự động inject reason khi requiresReason = true
                reason = tryAutoInjectReason(userAction, args);
                
                if (reason == null || reason.trim().isEmpty()) {
                    throw new RuntimeException(String.format(
                        "Reason is required for %s action on %s. Please provide a reason in the request body.", 
                        userAction.actionType(), 
                        determineTargetType(userAction, method, args)
                    ));
                }
            }
        }
        
        // Thực hiện action
        Object result = joinPoint.proceed();
        
        // Lấy entity mới sau khi thay đổi
        Object newEntity = getNewEntity(result, targetType, targetId);
        
        // Tự động lấy targetName
        String targetName = extractTargetName(newEntity != null ? newEntity : oldEntity);
        
        // So sánh thay đổi
        String changeSummary = compareChanges(clonedOldEntity, newEntity);
        
        // Tạo và lưu log
        UserActionLog actionLog = UserActionLog.builder()
                .userId(currentUser.getUserId())
                .username(currentUser.getUsername())
                .roleName(getRoleNames(currentUser))
                .actionType(userAction.actionType())
                .targetType(targetType)
                .targetId(targetId)
                .targetName(targetName)
                .description(userAction.description().isEmpty() ? generateDescription(userAction, targetType, targetName) : userAction.description())
                .reason(reason)
                .changeSummary(changeSummary)
                .ipAddress(getClientIpAddress())
                .userAgent(request.getHeader("User-Agent"))
                .actionTime(LocalDateTime.now())
                .build();
        
        try {
            userActionLogService.save(actionLog);
            log.info("User action logged: {} performed {} on {} [{}]", 
                    currentUser.getUsername(), userAction.actionType(), targetType, targetName);
        } catch (Exception e) {
            log.error("Failed to save user action log", e);
        }
        
        return result;
    }
    
    private User getCurrentUser() {
        try {
            return AccountUtils.getCurrentUser();
        } catch (Exception e) {
            log.warn("Cannot get current user: {}", e.getMessage());
            return null;
        }
    }
    
    private String getRoleNames(User user) {
        if (user == null || user.getRoles() == null || user.getRoles().isEmpty()) {
            return "UNKNOWN";
        }
        
        return user.getRoles().stream()
                .map(Role::getName)
                .reduce((r1, r2) -> r1 + ", " + r2)
                .orElse("UNKNOWN");
    }
    
    private String determineTargetType(UserAction userAction, Method method, Object[] args) {
        // Nếu annotation có targetType, dùng nó
        if (!userAction.targetType().isEmpty()) {
            return userAction.targetType().toUpperCase();
        }
        
        // Tự động xác định từ tên method
        String methodName = method.getName().toLowerCase();
        if (methodName.contains("role")) return "ROLE";
        if (methodName.contains("permission")) return "PERMISSION";
        if (methodName.contains("user")) return "USER";
        
        // Tự động xác định từ parameter type
        for (Object arg : args) {
            if (arg != null) {
                String className = arg.getClass().getSimpleName().toUpperCase();
                if (className.contains("ROLE")) return "ROLE";
                if (className.contains("PERMISSION")) return "PERMISSION";
                if (className.contains("USER")) return "USER";
            }
        }
        
        // Tự động xác định từ class chứa method
        String className = method.getDeclaringClass().getSimpleName().toLowerCase();
        if (className.contains("role")) return "ROLE";
        if (className.contains("permission")) return "PERMISSION";
        if (className.contains("user")) return "USER";
        
        return "UNKNOWN";
    }
    
    private String extractTargetId(Object[] args) {
        // Tìm ID từ parameters
        for (Object arg : args) {
            if (arg instanceof Long) {
                return arg.toString();
            } else if (arg instanceof Integer) {
                return arg.toString();
            } else if (arg instanceof String && arg.toString().matches("\\d+")) {
                return arg.toString();
            }
        }
        
        // Tìm ID từ entity object
        for (Object arg : args) {
            if (arg != null) {
                try {
                    Field idField = findIdField(arg.getClass());
                    if (idField != null) {
                        idField.setAccessible(true);
                        Object id = idField.get(arg);
                        if (id != null) {
                            return id.toString();
                        }
                    }
                } catch (Exception e) {
                    // Ignore
                }
            }
        }
        
        return null;
    }
    
    private Field findIdField(Class<?> clazz) {
        try {
            // Tìm field có @Id annotation
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(jakarta.persistence.Id.class)) {
                    return field;
                }
            }
            // Fallback: tìm field tên "id"
            return clazz.getDeclaredField("id");
        } catch (Exception e) {
            return null;
        }
    }
    
    private Object fetchOldEntity(String targetType, String targetId) {
        if (targetId == null) return null;
        
        try {
            Long longId = Long.valueOf(targetId);
            
            switch (targetType.toUpperCase()) {
                case "ROLE":
                    RoleRepository roleRepo = applicationContext.getBean(RoleRepository.class);
                    return roleRepo.findById(longId).orElse(null);
                case "PERMISSION":
                    PermissionRepository permRepo = applicationContext.getBean(PermissionRepository.class);
                    return permRepo.findById(longId).orElse(null);
                case "USER":
                    UserRepository userRepo = applicationContext.getBean(UserRepository.class);
                    return userRepo.findById(longId).orElse(null);
                default:
                    return null;
            }
        } catch (Exception e) {
            log.warn("Failed to fetch old entity: {}", e.getMessage());
            return null;
        }
    }
    
    private Object getNewEntity(Object result, String targetType, String targetId) {
        // Nếu result chính là entity, return nó
        if (result != null && isEntity(result)) {
            return result;
        }
        
        // Nếu không, fetch lại từ database
        return fetchOldEntity(targetType, targetId);
    }
    
    private boolean isEntity(Object obj) {
        return obj.getClass().isAnnotationPresent(jakarta.persistence.Entity.class);
    }
    
    private String extractTargetName(Object entity) {
        if (entity == null) return null;
        
        try {
            // Thử các field name phổ biến
            String[] nameFields = {"name", "roleName", "permissionName", "username", "title", "displayName"};
            
            for (String fieldName : nameFields) {
                try {
                    Field field = entity.getClass().getDeclaredField(fieldName);
                    field.setAccessible(true);
                    Object value = field.get(entity);
                    if (value != null) {
                        return value.toString();
                    }
                } catch (NoSuchFieldException e) {
                    // Field không tồn tại, thử field tiếp theo
                }
            }
            
            // Fallback: dùng toString() hoặc ID
            Field idField = findIdField(entity.getClass());
            if (idField != null) {
                idField.setAccessible(true);
                Object id = idField.get(entity);
                return "ID: " + (id != null ? id.toString() : "Unknown");
            }
            
        } catch (Exception e) {
            log.warn("Failed to extract target name: {}", e.getMessage());
        }
        
        return entity.getClass().getSimpleName();
    }
    
    private String extractReason(Object[] args) {
        for (Object arg : args) {
            if (arg != null) {
                // First priority: Check if argument extends BaseActionRequest
                if (arg instanceof com.example.demologin.dto.request.BaseActionRequest) {
                    com.example.demologin.dto.request.BaseActionRequest baseRequest = 
                        (com.example.demologin.dto.request.BaseActionRequest) arg;
                    if (baseRequest.hasReason()) {
                        return baseRequest.getReason();
                    }
                }
                
                // Second priority: Direct String parameter that might be reason
                if (arg instanceof String) {
                    String strArg = (String) arg;
                    if (strArg.length() > 10) { // Assume reason should be meaningful
                        return strArg;
                    }
                }
                
                // Third priority: Try reflection for "reason" field
                try {
                    Field reasonField = arg.getClass().getDeclaredField("reason");
                    reasonField.setAccessible(true);
                    Object reason = reasonField.get(arg);
                    if (reason != null && !reason.toString().trim().isEmpty()) {
                        return reason.toString();
                    }
                } catch (Exception e) {
                    // Ignore if field doesn't exist
                }
            }
        }
        return null;
    }
    
    /**
     * Try to auto-inject reason when requiresReason = true but no reason provided
     */
    private String tryAutoInjectReason(UserAction userAction, Object[] args) {
        // Auto-generate reason based on action and context
        String actionType = userAction.actionType().name().toLowerCase();
        String targetType = determineTargetType(userAction, null, args).toLowerCase();
        
        // Try to find BaseActionRequest to inject reason
        for (Object arg : args) {
            if (arg instanceof com.example.demologin.dto.request.BaseActionRequest) {
                com.example.demologin.dto.request.BaseActionRequest baseRequest = 
                    (com.example.demologin.dto.request.BaseActionRequest) arg;
                
                String autoReason = String.format("Auto-generated: %s %s operation", actionType, targetType);
                baseRequest.setReason(autoReason);
                
                log.info("Auto-injected reason for {}: {}", userAction.actionType(), autoReason);
                return autoReason;
            }
        }
        
        // Fallback: return default reason
        return String.format("System %s operation on %s", actionType, targetType);
    }
    
    private String compareChanges(Object oldObj, Object newObj) {
        if (oldObj == null && newObj == null) return "No data";
        if (oldObj == null) return "Created new record";
        if (newObj == null) return "Deleted record";
        
        StringBuilder changes = new StringBuilder();
        
        try {
            Field[] fields = oldObj.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                
                // Skip các field không cần theo dõi
                if (shouldSkipField(fieldName)) continue;
                
                Object oldValue = field.get(oldObj);
                Object newValue = field.get(newObj);
                
                if (!Objects.equals(oldValue, newValue)) {
                    changes.append(String.format("[%s: '%s' → '%s'] ", 
                            fieldName, 
                            oldValue != null ? oldValue : "null", 
                            newValue != null ? newValue : "null"));
                }
            }
        } catch (Exception e) {
            log.warn("Failed to compare changes: {}", e.getMessage());
            return "Cannot compare changes";
        }
        
        return changes.length() > 0 ? changes.toString() : "No changes detected";
    }
    
    private boolean shouldSkipField(String fieldName) {
        String[] skipFields = {"id", "createdAt", "updatedAt", "actionTime", "password", "passwordHash"};
        for (String skip : skipFields) {
            if (fieldName.toLowerCase().contains(skip.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    private String generateDescription(UserAction userAction, String targetType, String targetName) {
        return String.format("%s %s: %s", 
                userAction.actionType().name(), 
                targetType.toLowerCase(), 
                targetName != null ? targetName : "unknown");
    }
    
    private String getClientIpAddress() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
    
    private Object deepClone(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.readValue(objectMapper.writeValueAsBytes(obj), obj.getClass());
        } catch (Exception e) {
            log.warn("Failed to clone object: {}", e.getMessage());
            return null;
        }
    }
}
