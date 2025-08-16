package com.example.demologin.cache;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

@Component
public class CacheDetector {

    private static final Set<Class<? extends Annotation>> WRITE_ANNOTATIONS = Set.of(
            PostMapping.class, PutMapping.class,
            PatchMapping.class, DeleteMapping.class
    );

    public boolean isWriteOperation(ProceedingJoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        return WRITE_ANNOTATIONS.stream().anyMatch(method::isAnnotationPresent);
    }

    public String generateCacheKey(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getDeclaringType().getSimpleName() +
                ":" +
                signature.getMethod().getName();
    }

    public String getRelatedCacheKey(ProceedingJoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        if (method.getName().startsWith("create") ||
                method.getName().startsWith("update") ||
                method.getName().startsWith("delete")) {
            return method.getDeclaringClass().getSimpleName() + ":getAll";
        }
        return generateCacheKey(joinPoint);
    }
}
