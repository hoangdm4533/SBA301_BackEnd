package com.example.demologin.utils;

import java.util.regex.Pattern;

/**
 * Utility class for email validation using the same logic as ValidEmailValidator
 */
public class EmailUtils {
    
    private static final String EMAIL_PATTERN = 
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
        "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    
    /**
     * Check if a string is a valid email using the same validation logic as ValidEmailValidator
     * @param email The string to validate
     * @return true if the string is a valid email, false otherwise
     */
    public static boolean isValidEmail(String email) {
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
