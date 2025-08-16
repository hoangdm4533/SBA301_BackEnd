package com.example.demologin.aspect;

import com.example.demologin.cache.CacheDetector;
import com.example.demologin.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Aspect
@Component
@RequiredArgsConstructor
public class SmartCacheAspect {
    private static final Logger logger = LoggerFactory.getLogger(SmartCacheAspect.class);
    private static final ConcurrentMap<String, Object> CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, AtomicLong> DATA_VERSION = new ConcurrentHashMap<>();
    private static final AtomicLong GLOBAL_VERSION = new AtomicLong(0);

    private final CacheDetector detector;
    private final JwtUtil jwtUtil;

    @Around("@annotation(com.example.demologin.annotation.SmartCache)")
    public Object handleCache(ProceedingJoinPoint joinPoint) throws Throwable {
        String baseCacheKey = detector.generateCacheKey(joinPoint);
        String enhancedCacheKey = enhanceCacheKeyWithUserContext(baseCacheKey, joinPoint);

        boolean isWriteOperation = detector.isWriteOperation(joinPoint);

        // For read operations - check cache first
        if (!isWriteOperation) {
            Object cachedValue = CACHE.get(enhancedCacheKey);
            Long currentVersion = DATA_VERSION.get(enhancedCacheKey) != null ?
                    DATA_VERSION.get(enhancedCacheKey).get() : null;

            if (cachedValue != null && currentVersion != null) {
                logger.info("✅ [CACHE HIT] Key: {} | Version: {} | Cached Value Type: {}",
                        enhancedCacheKey,
                        currentVersion,
                        cachedValue.getClass().getSimpleName());
                return cachedValue;
            }
        }

        // Execute original method
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - startTime;

        // For write operations - update versions
        if (isWriteOperation) {
            handleWriteOperation(enhancedCacheKey, joinPoint, executionTime);
        } else {
            handleReadOperation(enhancedCacheKey, result, executionTime);
        }

        return result;
    }

    private String enhanceCacheKeyWithUserContext(String baseCacheKey, ProceedingJoinPoint joinPoint) {
        // Only enhance cache key for permission-related methods
        if (baseCacheKey.contains("RolePermissionServiceImpl") &&
                baseCacheKey.contains("getPermissionsForRoles")) {
            try {
                // Get current request from RequestContextHolder
                HttpServletRequest request =
                        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                                .getRequest();

                if (request != null) {
                    String token = request.getHeader("Authorization");
                    if (token != null && token.startsWith("Bearer ")) {
                        Set<String> roles = jwtUtil.extractRoles(token.substring(7));
                        return baseCacheKey + ":roles=" + String.join(",", roles);
                    }
                }
            } catch (IllegalStateException e) {
                logger.warn("No request bound to current thread, using basic cache key");
            } catch (Exception e) {
                logger.warn("Could not extract user roles for cache key enhancement", e);
            }
        }
        return baseCacheKey;
    }

    private void handleWriteOperation(String cacheKey, ProceedingJoinPoint joinPoint, long executionTime) {
        // Update global version
        long newVersion = GLOBAL_VERSION.incrementAndGet();

        // Get related cache keys
        String relatedCacheKey = detector.getRelatedCacheKey(joinPoint);

        // Update versions for both current and related keys
        DATA_VERSION.computeIfAbsent(cacheKey, k -> new AtomicLong(0))
                .set(newVersion);
        DATA_VERSION.computeIfAbsent(relatedCacheKey, k -> new AtomicLong(0))
                .set(newVersion);

        // Clear both caches
        CACHE.remove(cacheKey);
        CACHE.remove(relatedCacheKey);

        logger.info("🔄 [CACHE INVALIDATED] Key: {} and Related Key: {} | New Version: {} | Execution Time: {}ms",
                cacheKey, relatedCacheKey, newVersion, executionTime);
    }

    private void handleReadOperation(String cacheKey, Object result, long executionTime) {
        if (!CACHE.containsKey(cacheKey)) {
            DATA_VERSION.computeIfAbsent(cacheKey, k -> new AtomicLong(GLOBAL_VERSION.get()));
            CACHE.put(cacheKey, result);
            logger.info("📥 [CACHE STORED] Key: {} | Version: {} | Execution Time: {}ms | Value Type: {}",
                    cacheKey,
                    DATA_VERSION.get(cacheKey).get(),
                    executionTime,
                    result.getClass().getSimpleName());
        }
    }

    // Method to manually clear cache (can be called from other services)
    public static void clearCacheForKeys(String... keys) {
        for (String key : keys) {
            CACHE.remove(key);
            DATA_VERSION.remove(key);
        }
    }
}