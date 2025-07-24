package com.example.demologin.exception;

import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.exception.exceptions.AuthorizeException;
import com.example.demologin    .exception.exceptions.NotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class APIHandleException {

    // mỗi khi có lỗi validation thì chạy xử lý này

    //MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseObject> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Tạo danh sách thông báo lỗi
        List<String> errorMessages = new ArrayList<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMessages.add(error.getField() + ": " + error.getDefaultMessage());
        });

        // Gộp tất cả lỗi thành 1 message duy nhất
        String combinedMessage = String.join(", ", errorMessages);

        return ResponseEntity.badRequest().body(
                new ResponseObject(
                        HttpStatus.BAD_REQUEST.value(),
                        combinedMessage, // Gán tất cả lỗi vào message
                        null // Đặt data là null
                )
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseObject> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> errorMessages = new ArrayList<>();

        ex.getConstraintViolations().forEach(violation -> {
            String field = violation.getPropertyPath().toString();
            errorMessages.add(field + ": " + violation.getMessage());
        });

        String combinedMessage = String.join(", ", errorMessages);

        return ResponseEntity.badRequest().body(
                new ResponseObject(
                        HttpStatus.BAD_REQUEST.value(),
                        combinedMessage,
                        null
                )
        );
    }


    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity handleDuplicate(SQLIntegrityConstraintViolationException exception){
        return new ResponseEntity("Duplicate", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity handleNullPointer(NullPointerException exception){
        return new ResponseEntity(exception.getMessage(),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthorizeException.class)
    public ResponseEntity handleAuthenticateException(AuthorizeException exception){
        return new ResponseEntity(exception.getMessage(),HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity RuntimeExceptionException(RuntimeException exception){
        return new ResponseEntity(exception.getMessage(),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity NotFoundException(NotFoundException exception){
        return new ResponseEntity(exception.getMessage(),HttpStatus.NOT_FOUND);
    }



}
