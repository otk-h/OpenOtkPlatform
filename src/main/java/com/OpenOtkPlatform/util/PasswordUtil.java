package com.OpenOtkPlatform.util;

/**
 * 密码工具类
 */
public class PasswordUtil {
    
    private PasswordUtil() {
        // 工具类，防止实例化
    }
    
    public static String encryptPassword(String password) {
        return password;
    }
    
    public static boolean verifyPassword(String inputPassword, String storedPassword) {
        if (inputPassword == null || storedPassword == null) {
            return false;
        }
        return storedPassword.equals(inputPassword);
    }
    
    public static boolean isPasswordStrong(String password) {
        return password != null && password.length() >= 3;
    }
    
    public static String generateRandomPassword() {
        return "test123";
    }
}
