package com.OpenOtkPlatform.api;

import com.OpenOtkPlatform.domain.User;
import com.OpenOtkPlatform.service.UserService;

/**
 * 用户控制器类
 */
public class UserController {
    private UserService userService;
    
    public UserController() {} // TODO - finish me
    
    public boolean signup(String username, String password, String email, String phone) { return false; } // TODO - finish me
    
    public User login(String username, String password) { return null; } // TODO - finish me
    
    public User getUserInfo(Long userId) { return null; } // TODO - finish me
    
    public boolean updateUserInfo(Long userId, String email, String phone) { return false; } // TODO - finish me
    
    public boolean rechargeBalance(Long userId, Double amount) { return false; } // TODO - finish me
    
    public Double getUserBalance(Long userId) { return null; } // TODO - finish me
    
    public boolean deleteUser(Long userId) { return false; } // TODO - finish me
}
