package com.example.demologin.aspect;

import com.example.demologin.annotation.UserAction;
import com.example.demologin.entity.User;
import com.example.demologin.entity.Role;
import com.example.demologin.entity.UserActionLog;
import com.example.demologin.enums.UserActionType;
import com.example.demologin.service.UserActionLogService;
import com.example.demologin.utils.AccountUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserActionLogAspectTest {
    
    @Mock
    private UserActionLogService userActionLogService;
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private ApplicationContext applicationContext;
    
    @Mock
    private ProceedingJoinPoint joinPoint;
    
    @Mock
    private MethodSignature methodSignature;
    
    @InjectMocks
    private UserActionLogAspect userActionLogAspect;
    
    private User mockUser;
    private Role mockRole;
    
    @BeforeEach
    void setUp() {
        mockRole = new Role();
        mockRole.setName("ADMIN");
        
        Set<Role> roles = new HashSet<>();
        roles.add(mockRole);
        
        mockUser = new User();
        mockUser.setUserId(1L);
        mockUser.setUsername("testuser");
        mockUser.setRoles(roles);
        
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getHeader("User-Agent")).thenReturn("Test-Agent");
    }
    
    @Test
    void testLogUserAction_Success() throws Throwable {
        // Arrange
        Method testMethod = TestService.class.getMethod("testCreateAction", Long.class);
        
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(testMethod);
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L});
        when(joinPoint.proceed()).thenReturn("success");
        
        try (MockedStatic<AccountUtils> accountUtilsMocked = mockStatic(AccountUtils.class)) {
            accountUtilsMocked.when(AccountUtils::getCurrentUser).thenReturn(mockUser);
            
            // Act
            Object result = userActionLogAspect.logUserAction(joinPoint);
            
            // Assert
            verify(userActionLogService).save(any(UserActionLog.class));
            verify(joinPoint).proceed();
            assert result.equals("success");
        }
    }
    
    @Test
    void testLogUserAction_RequiredReasonMissing_ThrowsException() throws Throwable {
        // Arrange
        Method testMethod = TestService.class.getMethod("testDeleteActionWithReason", Long.class);
        
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(testMethod);
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L});
        
        try (MockedStatic<AccountUtils> accountUtilsMocked = mockStatic(AccountUtils.class)) {
            accountUtilsMocked.when(AccountUtils::getCurrentUser).thenReturn(mockUser);
            
            // Act & Assert
            try {
                userActionLogAspect.logUserAction(joinPoint);
            } catch (RuntimeException e) {
                assert e.getMessage().contains("Reason is required");
            }
        }
        
        verify(joinPoint, never()).proceed();
        verify(userActionLogService, never()).save(any());
    }
    
    @Test
    void testLogUserAction_NoCurrentUser_SkipsLogging() throws Throwable {
        // Arrange
        Method testMethod = TestService.class.getMethod("testCreateAction", Long.class);
        
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(testMethod);
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L});
        when(joinPoint.proceed()).thenReturn("success");
        
        try (MockedStatic<AccountUtils> accountUtilsMocked = mockStatic(AccountUtils.class)) {
            accountUtilsMocked.when(AccountUtils::getCurrentUser).thenThrow(new RuntimeException("No user"));
            
            // Act
            Object result = userActionLogAspect.logUserAction(joinPoint);
            
            // Assert
            verify(userActionLogService, never()).save(any());
            verify(joinPoint).proceed();
            assert result.equals("success");
        }
    }
    
    // Test service class for annotation testing
    static class TestService {
        
        @UserAction(actionType = UserActionType.CREATE, targetType = "ROLE", description = "Test create action")
        public String testCreateAction(Long id) {
            return "success";
        }
        
        @UserAction(actionType = UserActionType.DELETE, targetType = "ROLE", requiresReason = true, description = "Test delete action")
        public String testDeleteActionWithReason(Long id) {
            return "success";
        }
        
        @UserAction(actionType = UserActionType.UPDATE, description = "Test auto-detect action")
        public String testAutoDetectRoleAction(Long roleId) {
            return "success";
        }
    }
}
