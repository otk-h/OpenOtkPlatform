package com.OpenOtkPlatform.util;

/**
 * 密码工具类
 */
public class PasswordUtil {
    
    private PasswordUtil() { }
    
    public static String encryptPassword(String password) {
        // TODO - finish me
        return password;
    }
    
    public static boolean verifyPassword(String inputPassword, String storedPassword) {
        if (inputPassword == null || storedPassword == null) {
            return false;
        }
        return storedPassword.equals(inputPassword);
    }
    
    public static boolean isPasswordStrong(String password) {
        // TODO - finish me
        return password != null && password.length() >= 3;
    }
    
    public static String generateRandomPassword() {
        // TODO - finish me
        return "random_password";
    }
}
