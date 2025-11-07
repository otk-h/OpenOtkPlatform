package com.OpenOtkPlatform.util;

import java.util.regex.Pattern;

/**
 * 验证工具类
 */
public class ValidationUtil {
    
    private ValidationUtil() { }
    
    // 正则表达式模式
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern USERNAME_PATTERN = 
        Pattern.compile("^[a-zA-Z0-9_]{4,20}$");
    
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }
    
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username.trim()).matches();
    }
    
    public static boolean isValidPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        return password.length() >= 6 && password.length() <= 50;
    }
    
    public static boolean isValidPrice(Double price) {
        if (price == null) {
            return false;
        }
        // TODO - finish me
        // [0, 10000000]
        return price >= 0 && price <= 1000000;
    }
    
    public static boolean isValidStock(Integer stock) {
        if (stock == null) {
            return false;
        }
        // TODO - finish me
        // [0, 10000]
        return stock >= 0 && stock <= 10000;
    }
    
    public static boolean isValidItemName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        String trimmedName = name.trim();
        return trimmedName.length() >= 2 && trimmedName.length() <= 100;
    }
    
    public static boolean isValidItemDescription(String description) {
        if (description == null) {
            return false;
        }
        // TODO - finish me
        // [0, 1000]
        return description.length() <= 1000;
    }
    
    public static boolean isPositiveInteger(Integer number) {
        return number != null && number > 0;
    }
    
    public static boolean isPositiveDouble(Double number) {
        return number != null && number > 0;
    }
    
    public static boolean isNonNegativeInteger(Integer number) {
        return number != null && number >= 0;
    }
    
    public static boolean isNonNegativeDouble(Double number) {
        return number != null && number >= 0;
    }
    
    public static boolean isStringLengthValid(String str, int minLength, int maxLength) {
        if (str == null) { return false; }
        int length = str.trim().length();
        return length >= minLength && length <= maxLength;
    }
}
