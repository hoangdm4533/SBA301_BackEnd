package com.example.demologin.aspect;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SecuredEndpointAspectTest {
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
