package com.OpenOtkPlatform.persistence;

import com.OpenOtkPlatform.domain.User;
import java.util.List;

/**
 * 用户数据访问对象
 */
public class UserDAO {
    private DatabaseManager dbManager;
    
    public UserDAO() {} // TODO - finish me
    
    public boolean insertUser(User user) { return false; } // TODO - finish me
    
    public User getUserById(Long userId) { return null; } // TODO - finish me
    
    public User getUserByUsername(String username) { return null; } // TODO - finish me
    
    public User getUserByEmail(String email) { return null; } // TODO - finish me
    
    public List<User> getAllUsers() { return null; } // TODO - finish me
    
    public boolean updateUser(User user) { return false; } // TODO - finish me
    
    public boolean deleteUser(Long userId) { return false; } // TODO - finish me
    
    public boolean updateUserBalance(Long userId, Double newBalance) { return false; } // TODO - finish me
    
    public boolean isUsernameExists(String username) { return false; } // TODO - finish me
    
    public boolean isEmailExists(String email) { return false; } // TODO - finish me
}
