package com.OpenOtkPlatform.service;

import com.OpenOtkPlatform.domain.SystemLog;
import com.OpenOtkPlatform.repository.SystemLogRepository;
import com.OpenOtkPlatform.util.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogService {
    
    @Autowired
    private SystemLogRepository systemLogRepository;
    
    private Logger logger = Logger.getInstance();
    
    public void logUserOperation(String operationType, Long userId, String description) {
        if (operationType == null || operationType.trim().isEmpty() || 
            userId == null || userId <= 0 || 
            description == null || description.trim().isEmpty()) {
            return;
        }
        
        SystemLog log = new SystemLog(operationType, userId, description);
        systemLogRepository.save(log);
        
        logger.info(String.format("用户操作日志: [%s] 用户ID: %d, 描述: %s", 
            operationType, userId, description));
    }
    
    public void logSystemEvent(String operationType, String description) {
        if (operationType == null || operationType.trim().isEmpty() || 
            description == null || description.trim().isEmpty()) {
            return;
        }
        
        SystemLog log = new SystemLog(operationType, null, description);
        systemLogRepository.save(log);
        
        logger.info(String.format("系统事件日志: [%s] 描述: %s", operationType, description));
    }
    
    public void logLogin(Long userId) {
        logUserOperation(SystemLog.OPERATION_LOGIN, userId, "用户登录系统");
    }
    
    public void logRegister(Long userId) {
        logUserOperation(SystemLog.OPERATION_REGISTER, userId, "用户注册账号");
    }
    
    public void logItemPublish(Long userId, Long itemId) {
        logUserOperation(SystemLog.OPERATION_PUBLISH_ITEM, userId, 
            String.format("用户发布商品, 商品ID: %d", itemId));
    }
    
    public void logOrderCreate(Long userId, Long orderId) {
        logUserOperation(SystemLog.OPERATION_CREATE_ORDER, userId, 
            String.format("用户创建订单, 订单ID: %d", orderId));
    }
    
    public List<SystemLog> getLogsByUser(Long userId) {
        if (userId == null || userId <= 0) {
            return null;
        }
        return systemLogRepository.findByUserId(userId);
    }
    
    public List<SystemLog> getLogsByOperationType(String operationType) {
        if (operationType == null || operationType.trim().isEmpty()) {
            return null;
        }
        return systemLogRepository.findByOperationType(operationType);
    }
    
    public List<SystemLog> getAllLogs() {
        return systemLogRepository.findAll();
    }
    
    public boolean deleteLog(Long logId) {
        if (logId == null || logId <= 0) {
            return false;
        }
        try {
            systemLogRepository.deleteById(logId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
