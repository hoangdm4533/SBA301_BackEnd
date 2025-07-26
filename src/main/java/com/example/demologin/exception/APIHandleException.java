package com.example.demologin.exception;

import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.exception.exceptions.*;
import com.example.demologin.exception.exceptions.IllegalArgumentException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class APIHandleException {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseObject> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Lấy lỗi đầu tiên
        FieldError firstError = ex.getBindingResult().getFieldErrors().get(0);
        String message = firstError.getDefaultMessage();

        ResponseObject response = new ResponseObject(
                HttpStatus.BAD_REQUEST.value(),
                message,
                null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseObject> handleConstraintViolationException(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations()
                .iterator()
                .next()
                .getMessage(); // lấy lỗi đầu tiên

        ResponseObject response = new ResponseObject(
                HttpStatus.BAD_REQUEST.value(),
                message,
                null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseObject> handleBusinessException(BusinessException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ResponseObject(400, ex.getMessage(), null));
    }


    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ResponseObject> handleDuplicate(SQLIntegrityConstraintViolationException exception){
        return ResponseEntity.badRequest().body(
            new ResponseObject(
                HttpStatus.BAD_REQUEST.value(),
                "Duplicate",
                null
            )
        );
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ResponseObject> handleNullPointer(NullPointerException exception){
        return ResponseEntity.badRequest().body(
            new ResponseObject(
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                null
            )
        );
    }

    @ExceptionHandler(AuthorizeException.class)
    public ResponseEntity<ResponseObject> handleAuthenticateException(AuthorizeException exception){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            new ResponseObject(
                HttpStatus.UNAUTHORIZED.value(),
                exception.getMessage(),
                null
            )
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseObject> RuntimeExceptionException(RuntimeException exception){
        return ResponseEntity.badRequest().body(
            new ResponseObject(
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                null
            )
        );
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseObject> NotFoundException(NotFoundException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            new ResponseObject(
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                null
            )
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseObject> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.badRequest().body(new ResponseObject(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ResponseObject> handleConflict(ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseObject(HttpStatus.CONFLICT.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ResponseObject> handleFileStorage(FileStorageException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ResponseObject> handleForbidden(ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseObject(HttpStatus.FORBIDDEN.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseObject> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ResponseObject(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ResponseObject> handleInsufficientStock(InsufficientStockException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ResponseObject> handleInternalServerError(InternalServerErrorException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseObject> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ResponseObject> handleUnauthorized(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ResponseObject> handleValidation(ValidationException ex) {
        return ResponseEntity.badRequest().body(new ResponseObject(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<ResponseObject> handleTokenRefresh(TokenRefreshException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseObject(HttpStatus.FORBIDDEN.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(ArithmeticException.class)
    public ResponseEntity<ResponseObject> handleArithmetic(ArithmeticException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(ArrayIndexOutOfBoundsException.class)
    public ResponseEntity<ResponseObject> handleArrayIndex(ArrayIndexOutOfBoundsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(ClassCastException.class)
    public ResponseEntity<ResponseObject> handleClassCast(ClassCastException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ResponseObject> handleNumberFormat(NumberFormatException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ResponseObject> handleSecurity(SecurityException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseObject(HttpStatus.FORBIDDEN.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(StackOverflowError.class)
    public ResponseEntity<ResponseObject> handleStackOverflow(StackOverflowError ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Stack overflow error", null));
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ResponseObject> handleUnsupported(UnsupportedOperationException ex) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new ResponseObject(HttpStatus.NOT_IMPLEMENTED.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseObject> handleAll(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), null));
    }


}
