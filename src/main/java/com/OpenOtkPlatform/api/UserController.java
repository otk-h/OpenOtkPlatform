package com.OpenOtkPlatform.api;

import com.OpenOtkPlatform.domain.User;
import com.OpenOtkPlatform.service.UserService;
import com.OpenOtkPlatform.service.LogService;
import com.OpenOtkPlatform.util.ValidationUtil;

/**
 * 用户控制器类
 */
public class UserController {
    private UserService userService;
    private LogService logService;
    
    public UserController() {
        this.userService = UserService.getInstance();
        this.logService = LogService.getInstance();
    }
    
    public User getUserInfo(Long userId) {
        if (userId == null || userId <= 0) {
            return null;
        }
        
        User user = userService.getUserById(userId);
        if (user != null) {
            // 记录查询用户信息日志
            logService.logUserOperation("GET_USER_INFO", userId, "查询用户信息");
        }
        return user;
    }
    
    public boolean updateUserInfo(Long userId, String email, String phone) {
        if (userId == null || userId <= 0
            || email == null || email.trim().isEmpty()
            || phone == null || phone.trim().isEmpty()
            || !ValidationUtil.isValidEmail(email)
            || !ValidationUtil.isValidPhone(phone)
        ) {
            return false;
        }
        
        User user = userService.getUserById(userId);
        if (user == null) {
            return false;
        }
        
        // 检查邮箱是否已被其他用户使用
        if (!user.getEmail().equals(email) && userService.isEmailExists(email)) {
            return false;
        }
        
        user.setEmail(email);
        user.setPhone(phone);
        boolean success = userService.updateUser(user);
        
        if (success) {
            logService.logUserOperation("UPDATE_USER_INFO", userId, 
                String.format("更新用户信息，邮箱: %s, 电话: %s", email, phone));
        }
        
        return success;
    }
    
    public boolean rechargeBalance(Long userId, Double amount) {
        if (userId == null || userId <= 0
            || amount == null || amount <= 0
        ) {
            return false;
        }
        
        boolean success = userService.rechargeBalance(userId, amount);
        if (success) {
            logService.logUserOperation("RECHARGE_BALANCE", userId, 
                String.format("用户充值，金额: %.2f", amount));
        }
        return success;
    }
    
    public Double getUserBalance(Long userId) {
        if (userId == null || userId <= 0) {
            return null;
        }
        
        User user = userService.getUserById(userId);
        if (user != null) {
            logService.logUserOperation("GET_BALANCE", userId, "查询用户余额");
            return user.getBalance();
        }
        return null;
    }
    
    public boolean deleteUser(Long userId) {
        if (userId == null || userId <= 0) {
            return false;
        }
        
        boolean success = userService.deleteUser(userId);
        if (success) {
            logService.logUserOperation("DELETE_USER", userId, "删除用户账号");
        }
        return success;
    }
}
