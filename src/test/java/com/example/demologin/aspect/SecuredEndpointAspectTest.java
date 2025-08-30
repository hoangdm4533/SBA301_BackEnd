package com.example.demologin.aspect;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SecuredEndpointAspectTest {

	private org.aspectj.lang.Signature mockSignature(String name) {
		return new org.aspectj.lang.Signature() {
			@Override public String toShortString() { return name; }
			@Override public String toLongString() { return name; }
			@Override public String getName() { return name; }
			@Override public int getModifiers() { return 0; }
			@Override public Class<?> getDeclaringType() { return Object.class; }
			@Override public String getDeclaringTypeName() { return "Object"; }
		};
	}

	@Test
	void testHandleTokenValidationException_expired() {
		var joinPoint = mock(org.aspectj.lang.ProceedingJoinPoint.class);
		when(joinPoint.getSignature()).thenReturn(mockSignature("testMethod"));
		var ex = new com.example.demologin.exception.exceptions.TokenValidationException(
				"msg", com.example.demologin.exception.exceptions.TokenValidationException.TokenErrorType.EXPIRED);
		String msg = invokeHandleTokenValidationException(aspect, ex, joinPoint);
		assertEquals("Token has expired", msg);
	}

	@Test
	void testHandleTokenValidationException_invalidSignature() {
		var joinPoint = mock(org.aspectj.lang.ProceedingJoinPoint.class);
		when(joinPoint.getSignature()).thenReturn(mockSignature("testMethod"));
		var ex = new com.example.demologin.exception.exceptions.TokenValidationException(
				"msg", com.example.demologin.exception.exceptions.TokenValidationException.TokenErrorType.INVALID_SIGNATURE);
		String msg = invokeHandleTokenValidationException(aspect, ex, joinPoint);
		assertEquals("Invalid token signature", msg);
	}

	@Test
	void testHandleTokenValidationException_missingPermissions() {
		var joinPoint = mock(org.aspectj.lang.ProceedingJoinPoint.class);
		when(joinPoint.getSignature()).thenReturn(mockSignature("testMethod"));
		var ex = new com.example.demologin.exception.exceptions.TokenValidationException(
				"msg", com.example.demologin.exception.exceptions.TokenValidationException.TokenErrorType.MISSING_PERMISSIONS);
		String msg = invokeHandleTokenValidationException(aspect, ex, joinPoint);
		assertEquals("Invalid token permissions format", msg);
	}

	@Test
	void testHandleTokenValidationException_malformed() {
		var joinPoint = mock(org.aspectj.lang.ProceedingJoinPoint.class);
		when(joinPoint.getSignature()).thenReturn(mockSignature("testMethod"));
		var ex = new com.example.demologin.exception.exceptions.TokenValidationException(
				"msg", com.example.demologin.exception.exceptions.TokenValidationException.TokenErrorType.MALFORMED);
		String msg = invokeHandleTokenValidationException(aspect, ex, joinPoint);
		assertEquals("Malformed token", msg);
	}

	@Test
	void testHandleTokenValidationException_default() {
		var joinPoint = mock(org.aspectj.lang.ProceedingJoinPoint.class);
		when(joinPoint.getSignature()).thenReturn(mockSignature("testMethod"));
		var ex = new com.example.demologin.exception.exceptions.TokenValidationException(
				"msg", com.example.demologin.exception.exceptions.TokenValidationException.TokenErrorType.GENERAL_ERROR);
		String msg = invokeHandleTokenValidationException(aspect, ex, joinPoint);
		assertEquals("Token validation failed", msg);
	}

	// Helper to access private method
	private String invokeHandleTokenValidationException(SecuredEndpointAspect aspect, com.example.demologin.exception.exceptions.TokenValidationException ex, org.aspectj.lang.ProceedingJoinPoint joinPoint) {
		try {
			java.lang.reflect.Method m = SecuredEndpointAspect.class.getDeclaredMethod("handleTokenValidationException", com.example.demologin.exception.exceptions.TokenValidationException.class, org.aspectj.lang.ProceedingJoinPoint.class);
			m.setAccessible(true);
			return (String) m.invoke(aspect, ex, joinPoint);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	@InjectMocks
	private SecuredEndpointAspect aspect;

	@org.mockito.Mock
	private jakarta.servlet.http.HttpServletRequest request;
	@org.mockito.Mock
	private com.example.demologin.utils.JwtUtil jwtUtil;
	@org.mockito.Mock
	private com.example.demologin.service.RolePermissionService rolePermissionService;

	@Test
	void testAspectNotNull() {
		assertNotNull(aspect);
	}

	@Test
	void testCheckSecuredEndpoint_noAnnotation() throws Throwable {
		var joinPoint = mock(org.aspectj.lang.ProceedingJoinPoint.class);
		var methodSignature = mock(org.aspectj.lang.reflect.MethodSignature.class);
	lenient().when(joinPoint.getSignature()).thenReturn(methodSignature);
	lenient().when(methodSignature.getMethod()).thenReturn(Object.class.getMethod("toString"));
		assertThrows(com.example.demologin.exception.exceptions.AccessDenyException.class,
			() -> aspect.checkSecuredEndpoint(joinPoint, null));
	}

	@Test
	void testCheckSecuredEndpoint_unauthenticated() throws Throwable {
		var joinPoint = mock(org.aspectj.lang.ProceedingJoinPoint.class);
		var annotation = mock(com.example.demologin.annotation.SecuredEndpoint.class);
		var methodSignature = mock(org.aspectj.lang.reflect.MethodSignature.class);
	lenient().when(joinPoint.getSignature()).thenReturn(methodSignature);
	lenient().when(methodSignature.getMethod()).thenReturn(Object.class.getMethod("toString"));
	lenient().when(annotation.value()).thenReturn("PERM");
		org.springframework.security.core.context.SecurityContextHolder.clearContext();
		assertThrows(com.example.demologin.exception.exceptions.UserNotAuthenticatedException.class,
			() -> aspect.checkSecuredEndpoint(joinPoint, annotation));
	}

	@Test
	void testCheckSecuredEndpoint_missingToken() throws Throwable {
		var joinPoint = mock(org.aspectj.lang.ProceedingJoinPoint.class);
		var annotation = mock(com.example.demologin.annotation.SecuredEndpoint.class);
		var methodSignature = mock(org.aspectj.lang.reflect.MethodSignature.class);
	lenient().when(joinPoint.getSignature()).thenReturn(methodSignature);
	lenient().when(methodSignature.getMethod()).thenReturn(Object.class.getMethod("toString"));
	lenient().when(annotation.value()).thenReturn("PERM");
		org.springframework.security.core.Authentication auth = mock(org.springframework.security.core.Authentication.class);
		when(auth.isAuthenticated()).thenReturn(true);
		org.springframework.security.core.context.SecurityContext context = mock(org.springframework.security.core.context.SecurityContext.class);
		when(context.getAuthentication()).thenReturn(auth);
		org.springframework.security.core.context.SecurityContextHolder.setContext(context);
		when(request.getHeader("Authorization")).thenReturn(null);
		assertThrows(com.example.demologin.exception.exceptions.InvalidTokenException.class,
			() -> aspect.checkSecuredEndpoint(joinPoint, annotation));
	}

	@Test
	void testCheckSecuredEndpoint_permissionDenied() throws Throwable {
		var joinPoint = mock(org.aspectj.lang.ProceedingJoinPoint.class);
		var annotation = mock(com.example.demologin.annotation.SecuredEndpoint.class);
		var methodSignature = mock(org.aspectj.lang.reflect.MethodSignature.class);
	lenient().when(joinPoint.getSignature()).thenReturn(methodSignature);
	lenient().when(methodSignature.getMethod()).thenReturn(Object.class.getMethod("toString"));
	lenient().when(annotation.value()).thenReturn("PERM");
		org.springframework.security.core.Authentication auth = mock(org.springframework.security.core.Authentication.class);
		when(auth.isAuthenticated()).thenReturn(true);
		org.springframework.security.core.context.SecurityContext context = mock(org.springframework.security.core.context.SecurityContext.class);
		when(context.getAuthentication()).thenReturn(auth);
		org.springframework.security.core.context.SecurityContextHolder.setContext(context);
		when(request.getHeader("Authorization")).thenReturn("Bearer token");
		when(jwtUtil.extractRoles(anyString())).thenReturn(java.util.Set.of("ROLE_USER"));
		when(jwtUtil.extractUsernameWithValidation(anyString())).thenReturn("user");
		when(rolePermissionService.getPermissionsForRoles(anySet())).thenReturn(java.util.Set.of("OTHER"));
		assertThrows(com.example.demologin.exception.exceptions.AccessDenyException.class,
			() -> aspect.checkSecuredEndpoint(joinPoint, annotation));
	}

	@Test
	void testCheckSecuredEndpoint_permissionGranted() throws Throwable {
		var joinPoint = mock(org.aspectj.lang.ProceedingJoinPoint.class);
		var annotation = mock(com.example.demologin.annotation.SecuredEndpoint.class);
		var methodSignature = mock(org.aspectj.lang.reflect.MethodSignature.class);
	lenient().when(joinPoint.getSignature()).thenReturn(methodSignature);
	lenient().when(methodSignature.getMethod()).thenReturn(Object.class.getMethod("toString"));
	lenient().when(annotation.value()).thenReturn("PERM");
		org.springframework.security.core.Authentication auth = mock(org.springframework.security.core.Authentication.class);
		when(auth.isAuthenticated()).thenReturn(true);
		org.springframework.security.core.context.SecurityContext context = mock(org.springframework.security.core.context.SecurityContext.class);
		when(context.getAuthentication()).thenReturn(auth);
		org.springframework.security.core.context.SecurityContextHolder.setContext(context);
		when(request.getHeader("Authorization")).thenReturn("Bearer token");
		when(jwtUtil.extractRoles(anyString())).thenReturn(java.util.Set.of("ROLE_USER"));
		when(jwtUtil.extractUsernameWithValidation(anyString())).thenReturn("user");
		when(rolePermissionService.getPermissionsForRoles(anySet())).thenReturn(java.util.Set.of("PERM"));
		when(joinPoint.proceed()).thenReturn("ok");
		Object result = aspect.checkSecuredEndpoint(joinPoint, annotation);
		assertEquals("ok", result);
	}
}
