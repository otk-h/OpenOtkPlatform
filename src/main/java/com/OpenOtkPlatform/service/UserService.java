package com.OpenOtkPlatform.service;

import com.OpenOtkPlatform.domain.User;
import com.OpenOtkPlatform.persistence.UserDAO;
import com.OpenOtkPlatform.util.PasswordUtil;
import com.OpenOtkPlatform.util.ValidationUtil;

/**
 * 用户服务类 - 单例模式
 */
public class UserService {
    private static UserService instance;
    private UserDAO userDAO;
    
    private UserService() {
        this.userDAO = new UserDAO();
    }
    
    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }
    
    public boolean register(String username, String password, String email, String phone) {
        if (username == null || username.trim().isEmpty()
            || password == null || password.trim().isEmpty()
            || email == null || email.trim().isEmpty()
            || phone == null || phone.trim().isEmpty()
            || isUsernameExists(username)
            || isEmailExists(email)
            || !ValidationUtil.isValidEmail(email)
            || !ValidationUtil.isValidPhone(phone)
        ) {
            return false;
        }
        
        String encryptedPassword = PasswordUtil.encryptPassword(password);
        User newUser = new User(username, encryptedPassword, email, phone);
        
        return userDAO.insertUser(newUser);
    }
    
    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty()
            || password == null || password.trim().isEmpty()
        ) {
            return null;
        }
        
        User user = userDAO.getUserByUsername(username);
        if (user != null && PasswordUtil.verifyPassword(password, user.getPassword())) {
            return user;
        }
        return null;
    }
    
    public User getUserById(Long userId) {
        if (userId == null || userId <= 0) {
            return null;
        }
        return userDAO.getUserById(userId);
    }
    
    public User getUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        return userDAO.getUserByUsername(username);
    }
    
    public boolean updateUser(User user) {
        if (user == null || user.getId() == null) {
            return false;
        }
        return userDAO.updateUser(user);
    }
    
    public boolean deleteUser(Long userId) {
        if (userId == null || userId <= 0) {
            return false;
        }
        return userDAO.deleteUser(userId);
    }
    
    public boolean isUsernameExists(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return userDAO.isUsernameExists(username);
    }
    
    public boolean isEmailExists(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return userDAO.isEmailExists(email);
    }
    
    public boolean rechargeBalance(Long userId, Double amount) {
        if (userId == null || userId <= 0
            || amount == null || amount <= 0
        ) {
            return false;
        }
        
        User user = userDAO.getUserById(userId);
        if (user == null) {
            return false;
        }
        
        user.addBalance(amount);
        return userDAO.updateUserBalance(userId, user.getBalance());
    }
    
    public boolean deductBalance(Long userId, Double amount) {
        if (userId == null || userId <= 0
            || amount == null || amount <= 0
        ) {
            return false;
        }
        
        User user = userDAO.getUserById(userId);
        if (user == null) {
            return false;
        }
        
        if (user.deductBalance(amount)) {
            return userDAO.updateUserBalance(userId, user.getBalance());
        }
        return false;
    }
}
