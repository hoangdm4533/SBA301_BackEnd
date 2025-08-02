package com.example.demologin.aspect;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.dto.response.ResponseObject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ApiResponseAspect {

    @Around("@annotation(apiResponse)")
    public Object handleApiResponse(ProceedingJoinPoint joinPoint, ApiResponse apiResponse) throws Throwable {
        // Gọi method gốc để lấy data thuần (DTO)
        Object data = joinPoint.proceed();
        
        // Lấy message và status từ annotation
        String message = apiResponse.message();
        HttpStatus status = apiResponse.status();
        
        // Tạo ResponseObject với data từ service
        ResponseObject responseObject = new ResponseObject(
            status.value(),
            message,
            data
        );
        
        // Trả về ResponseEntity với ResponseObject
        return ResponseEntity.status(status).body(responseObject);
    }
}
