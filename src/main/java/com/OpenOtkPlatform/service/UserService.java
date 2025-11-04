package com.OpenOtkPlatform.service;

import com.OpenOtkPlatform.domain.User;

/**
 * 用户服务类 - 单例模式
 */
public class UserService {
    private static UserService instance;
    
    private UserService() {} // TODO - finish me
    
    public static UserService getInstance() { return null; } // TODO - finish me
    
    public boolean register(String username, String password, String email, String phone) { return false; } // TODO - finish me
    
    public User login(String username, String password) { return null; } // TODO - finish me
    
    public User getUserById(Long userId) { return null; } // TODO - finish me
    
    public User getUserByUsername(String username) { return null; } // TODO - finish me
    
    public boolean updateUser(User user) { return false; } // TODO - finish me
    
    public boolean deleteUser(Long userId) { return false; } // TODO - finish me
    
    public boolean isUsernameExists(String username) { return false; } // TODO - finish me
    
    public boolean isEmailExists(String email) { return false; } // TODO - finish me
    
    public boolean rechargeBalance(Long userId, Double amount) { return false; } // TODO - finish me
    
    public boolean deductBalance(Long userId, Double amount) { return false; } // TODO - finish me
}
