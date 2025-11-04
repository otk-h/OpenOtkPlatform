package com.OpenOtkPlatform.service;

import com.OpenOtkPlatform.domain.User;
import com.OpenOtkPlatform.repository.UserRepository;
import com.OpenOtkPlatform.util.PasswordUtil;
import com.OpenOtkPlatform.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
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
        
        try {
            userRepository.save(newUser);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty()
            || password == null || password.trim().isEmpty()
        ) {
            return null;
        }
        
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent() && PasswordUtil.verifyPassword(password, userOpt.get().getPassword())) {
            return userOpt.get();
        }
        return null;
    }
    
    public User getUserById(Long userId) {
        if (userId == null || userId <= 0) {
            return null;
        }
        return userRepository.findById(userId).orElse(null);
    }
    
    public User getUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        return userRepository.findByUsername(username).orElse(null);
    }
    
    public boolean updateUser(User user) {
        if (user == null || user.getId() == null) {
            return false;
        }
        try {
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean deleteUser(Long userId) {
        if (userId == null || userId <= 0) {
            return false;
        }
        try {
            userRepository.deleteById(userId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isUsernameExists(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return userRepository.existsByUsername(username);
    }
    
    public boolean isEmailExists(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return userRepository.existsByEmail(email);
    }
    
    public boolean rechargeBalance(Long userId, Double amount) {
        if (userId == null || userId <= 0
            || amount == null || amount <= 0
        ) {
            return false;
        }
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return false;
        }
        
        User user = userOpt.get();
        user.addBalance(amount);
        try {
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean deductBalance(Long userId, Double amount) {
        if (userId == null || userId <= 0
            || amount == null || amount <= 0
        ) {
            return false;
        }
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return false;
        }
        
        User user = userOpt.get();
        if (user.deductBalance(amount)) {
            try {
                userRepository.save(user);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
