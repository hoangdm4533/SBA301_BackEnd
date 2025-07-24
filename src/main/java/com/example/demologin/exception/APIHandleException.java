package com.example.demologin.exception;

import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.exception.exceptions.AuthorizeException;
import com.example.demologin    .exception.exceptions.NotFoundException;
import com.example.demologin.exception.exceptions.BadRequestException;
import com.example.demologin.exception.exceptions.ConflictException;
import com.example.demologin.exception.exceptions.FileStorageException;
import com.example.demologin.exception.exceptions.ForbiddenException;
import com.example.demologin.exception.exceptions.IllegalArgumentException;
import com.example.demologin.exception.exceptions.InsufficientStockException;
import com.example.demologin.exception.exceptions.InternalServerErrorException;
import com.example.demologin.exception.exceptions.ResourceNotFoundException;
import com.example.demologin.exception.exceptions.UnauthorizedException;
import com.example.demologin.exception.exceptions.ValidationException;
import com.example.demologin.exception.exceptions.TokenRefreshException;
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
