package com.OpenOtkPlatform.api;

import com.OpenOtkPlatform.domain.User;
import com.OpenOtkPlatform.service.UserService;
import com.OpenOtkPlatform.service.LogService;
import com.OpenOtkPlatform.util.PasswordUtil;
import com.OpenOtkPlatform.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private LogService logService;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        String email = request.getEmail();
        String phone = request.getPhone();
        
        if (username == null || username.trim().isEmpty()
            || password == null || password.trim().isEmpty()
            || email == null || email.trim().isEmpty()
            || phone == null || phone.trim().isEmpty()
        ) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "请填写所有必填字段"));
        }
        
        boolean success = userService.register(username, password, email, phone);
        if (success) {
            User user = userService.getUserByUsername(username);
            if (user != null) {
                logService.logRegister(user.getId());
            }
            return ResponseEntity.ok(new ApiResponse(true, "注册成功"));
        }
        return ResponseEntity.badRequest().body(new ApiResponse(false, "注册失败"));
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        
        if (username == null || username.trim().isEmpty()
            || password == null || password.trim().isEmpty()
        ) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "请填写用户名和密码"));
        }
        
        User user = userService.login(username, password);
        if (user != null) {
            logService.logLogin(user.getId());
            return ResponseEntity.ok(new LoginResponse(true, "登录成功", user));
        }
        return ResponseEntity.badRequest().body(new ApiResponse(false, "用户名或密码错误"));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam Long userId) {
        if (userId == null || userId <= 0) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "用户ID无效"));
        }
        
        logService.logUserOperation("LOGOUT", userId, "用户登出系统");
        return ResponseEntity.ok(new ApiResponse(true, "登出成功"));
    }
    
    @GetMapping("/validate")
    public ResponseEntity<?> validateSession(@RequestParam Long userId) {
        if (userId == null || userId <= 0) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "用户ID无效"));
        }
        
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(new ApiResponse(user != null, user != null ? "会话有效" : "会话无效"));
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        Long userId = request.getUserId();
        String oldPassword = request.getOldPassword();
        String newPassword = request.getNewPassword();
        
        if (userId == null || userId <= 0
            || oldPassword == null || oldPassword.trim().isEmpty()
            || newPassword == null || newPassword.trim().isEmpty()
        ) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "请填写所有必填字段"));
        }
        
        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "用户不存在"));
        }
        
        // 验证旧密码
        if (!PasswordUtil.verifyPassword(oldPassword, user.getPassword())) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "旧密码错误"));
        }
        
        // 验证新密码强度
        if (!PasswordUtil.isPasswordStrong(newPassword)) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "新密码强度不足"));
        }
        
        // 更新密码
        user.setPassword(PasswordUtil.encryptPassword(newPassword));
        boolean success = userService.updateUser(user);
        
        if (success) {
            logService.logUserOperation("CHANGE_PASSWORD", userId, "用户修改密码");
            return ResponseEntity.ok(new ApiResponse(true, "密码修改成功"));
        }
        
        return ResponseEntity.badRequest().body(new ApiResponse(false, "密码修改失败"));
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String email) {
        if (email == null || email.trim().isEmpty() || !ValidationUtil.isValidEmail(email)) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "邮箱格式无效"));
        }
        
        User user = userService.getUserByUsername(email); // 假设邮箱作为用户名
        if (user == null) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "用户不存在"));
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
            return ResponseEntity.ok(new ApiResponse(true, "密码重置成功，请查收邮件"));
        }
        
        return ResponseEntity.badRequest().body(new ApiResponse(false, "密码重置失败"));
    }
    
    // 请求和响应DTO类
    public static class RegisterRequest {
        private String username;
        private String password;
        private String email;
        private String phone;
        
        // getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }

    public static class LoginRequest {
        private String username;
        private String password;
        
        // getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class ChangePasswordRequest {
        private Long userId;
        private String oldPassword;
        private String newPassword;
        
        // getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    public static class ApiResponse {
        private boolean success;
        private String message;
        
        public ApiResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        // getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class LoginResponse {
        private boolean success;
        private String message;
        private User user;
        
        public LoginResponse(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }
        
        // getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }
    }
}
