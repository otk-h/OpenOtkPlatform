package com.OpenOtkPlatform.api;

import com.OpenOtkPlatform.domain.User;
import com.OpenOtkPlatform.service.UserService;
import com.OpenOtkPlatform.service.LogService;
import com.OpenOtkPlatform.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private LogService logService;
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserInfo(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "用户ID无效"));
        }
        
        User user = userService.getUserById(id);
        if (user != null) {
            // 记录查询用户信息日志
            logService.logUserOperation("GET_USER_INFO", id, "查询用户信息");
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserInfo(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        String email = request.getEmail();
        String phone = request.getPhone();
        
        if (id == null || id <= 0
            || email == null || email.trim().isEmpty()
            || phone == null || phone.trim().isEmpty()
            || !ValidationUtil.isValidEmail(email)
            || !ValidationUtil.isValidPhone(phone)
        ) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "参数无效"));
        }
        
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        // 检查邮箱是否已被其他用户使用
        if (!user.getEmail().equals(email) && userService.isEmailExists(email)) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "邮箱已被使用"));
        }
        
        user.setEmail(email);
        user.setPhone(phone);
        boolean success = userService.updateUser(user);
        
        if (success) {
            logService.logUserOperation("UPDATE_USER_INFO", id, 
                String.format("更新用户信息，邮箱: %s, 电话: %s", email, phone));
            return ResponseEntity.ok(new ApiResponse(true, "用户信息更新成功"));
        }
        
        return ResponseEntity.badRequest().body(new ApiResponse(false, "用户信息更新失败"));
    }
    
    @PostMapping("/{id}/recharge")
    public ResponseEntity<?> rechargeBalance(@PathVariable Long id, @RequestParam Double amount) {
        if (id == null || id <= 0
            || amount == null || amount <= 0
        ) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "参数无效"));
        }
        
        boolean success = userService.rechargeBalance(id, amount);
        if (success) {
            logService.logUserOperation("RECHARGE_BALANCE", id, 
                String.format("用户充值，金额: %.2f", amount));
            return ResponseEntity.ok(new ApiResponse(true, "充值成功"));
        }
        return ResponseEntity.badRequest().body(new ApiResponse(false, "充值失败"));
    }
    
    @GetMapping("/{id}/balance")
    public ResponseEntity<?> getUserBalance(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "用户ID无效"));
        }
        
        User user = userService.getUserById(id);
        if (user != null) {
            logService.logUserOperation("GET_BALANCE", id, "查询用户余额");
            return ResponseEntity.ok(new BalanceResponse(user.getBalance()));
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "用户ID无效"));
        }
        
        boolean success = userService.deleteUser(id);
        if (success) {
            logService.logUserOperation("DELETE_USER", id, "删除用户账号");
            return ResponseEntity.ok(new ApiResponse(true, "用户删除成功"));
        }
        return ResponseEntity.badRequest().body(new ApiResponse(false, "用户删除失败"));
    }
    
    // 请求DTO类
    public static class UpdateUserRequest {
        private String email;
        private String phone;
        
        // getters and setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
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

    public static class BalanceResponse {
        private Double balance;
        
        public BalanceResponse(Double balance) {
            this.balance = balance;
        }
        
        // getters and setters
        public Double getBalance() { return balance; }
        public void setBalance(Double balance) { this.balance = balance; }
    }
}
