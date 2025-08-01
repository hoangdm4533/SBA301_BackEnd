package com.example.demologin.validator;

import com.example.demologin.annotation.ValidEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class ValidEmailValidator implements ConstraintValidator<ValidEmail, String> {
    
    private static final String EMAIL_PATTERN = 
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
        "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    
    @Override
    public void initialize(ValidEmail constraintAnnotation) {
        // Initialization logic if needed
    }
    
    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        // Check basic email format
        if (!pattern.matcher(email).matches()) {
            return false;
        }
        
        // Additional validations
        if (email.length() > 320) { // RFC 5321 email length limit
            return false;
        }
        
        // Check for common invalid patterns
        if (email.contains("..") || email.startsWith(".") || email.endsWith(".")) {
            return false;
        }
        
        return true;
    }
}
