package com.OpenOtkPlatform.api;

import com.OpenOtkPlatform.domain.User;
import com.OpenOtkPlatform.service.UserService;
import com.OpenOtkPlatform.service.LogService;
import com.OpenOtkPlatform.util.PasswordUtil;
import com.OpenOtkPlatform.util.ValidationUtil;

/**
 * 认证控制器类 - 专门处理登录注册
 */
public class AuthController {
    private UserService userService;
    private LogService logService;
    
    public AuthController() {
        this.userService = UserService.getInstance();
        this.logService = LogService.getInstance();
    }
    
    public boolean register(String username, String password, String email, String phone) {
        if (username == null || username.trim().isEmpty()
            || password == null || password.trim().isEmpty()
            || email == null || email.trim().isEmpty()
            || phone == null || phone.trim().isEmpty()
        ) {
            return false;
        }
        
        boolean success = userService.register(username, password, email, phone);
        if (success) {
            User user = userService.getUserByUsername(username);
            if (user != null) {
                logService.logRegister(user.getId());
            }
        }
        return success;
    }
    
    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty()
            || password == null || password.trim().isEmpty()
        ) {
            return null;
        }
        
        User user = userService.login(username, password);
        if (user != null) {
            logService.logLogin(user.getId());
        }
        return user;
    }
    
    public boolean logout(Long userId) {
        if (userId == null || userId <= 0) {
            return false;
        }
        
        logService.logUserOperation("LOGOUT", userId, "用户登出系统");
        return true;
    }
    
    public boolean validateSession(Long userId) {
        if (userId == null || userId <= 0) {
            return false;
        }
        
        User user = userService.getUserById(userId);
        return user != null;
    }
    
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        if (userId == null || userId <= 0
            || oldPassword == null || oldPassword.trim().isEmpty()
            || newPassword == null || newPassword.trim().isEmpty()
        ) {
            return false;
        }
        
        User user = userService.getUserById(userId);
        if (user == null) {
            return false;
        }
        
        // 验证旧密码
        if (!PasswordUtil.verifyPassword(oldPassword, user.getPassword())) {
            return false;
        }
        
        // 验证新密码强度
        if (!PasswordUtil.isPasswordStrong(newPassword)) {
            return false;
        }
        
        // 更新密码
        user.setPassword(PasswordUtil.encryptPassword(newPassword));
        boolean success = userService.updateUser(user);
        
        if (success) {
            logService.logUserOperation("CHANGE_PASSWORD", userId, "用户修改密码");
        }
        
        return success;
    }
    
    public boolean resetPassword(String email) {
        if (email == null || email.trim().isEmpty() || !ValidationUtil.isValidEmail(email)) {
            return false;
        }
        
        User user = userService.getUserByUsername(email); // 假设邮箱作为用户名
        if (user == null) {
            return false;
        }
        
        // 生成随机密码
        String newPassword = PasswordUtil.generateRandomPassword();
        user.setPassword(PasswordUtil.encryptPassword(newPassword));
        boolean success = userService.updateUser(user);
        
        if (success) {
            // 这里应该发送邮件给用户，包含新密码
            // 为了简化，这里只记录日志
            logService.logUserOperation("RESET_PASSWORD", user.getId(), 
                String.format("用户重置密码，新密码: %s", newPassword));
        }
        
        return success;
    }
}
