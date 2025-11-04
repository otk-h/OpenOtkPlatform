package com.OpenOtkPlatform.service;

import com.OpenOtkPlatform.domain.SystemLog;
import com.OpenOtkPlatform.persistence.LogDAO;
import com.OpenOtkPlatform.util.Logger;
import java.util.List;

/**
 * 日志服务类 - 单例模式 + 观察者模式
 */
public class LogService {
    private static LogService instance;
    private LogDAO logDAO;
    private Logger logger;
    
    private LogService() {
        this.logDAO = new LogDAO();
        this.logger = Logger.getInstance();
    }
    
    public static LogService getInstance() {
        if (instance == null) {
            instance = new LogService();
        }
        return instance;
    }
    
    public void logUserOperation(String operationType, Long userId, String description) {
        if (operationType == null || operationType.trim().isEmpty() || 
            userId == null || userId <= 0 || 
            description == null || description.trim().isEmpty()) {
            return;
        }
        
        SystemLog log = new SystemLog(operationType, userId, description);
        logDAO.insertLog(log);
        
        logger.info(String.format("用户操作日志: [%s] 用户ID: %d, 描述: %s", 
            operationType, userId, description));
    }
    
    public void logSystemEvent(String operationType, String description) {
        if (operationType == null || operationType.trim().isEmpty() || 
            description == null || description.trim().isEmpty()) {
            return;
        }
        
        SystemLog log = new SystemLog(operationType, null, description);
        logDAO.insertLog(log);
        
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
        return logDAO.getLogsByUser(userId);
    }
    
    public List<SystemLog> getLogsByOperationType(String operationType) {
        if (operationType == null || operationType.trim().isEmpty()) {
            return null;
        }
        return logDAO.getLogsByOperationType(operationType);
    }
    
    public List<SystemLog> getAllLogs() {
        return logDAO.getAllLogs();
    }
    
    public boolean deleteLog(Long logId) {
        if (logId == null || logId <= 0) {
            return false;
        }
        return logDAO.deleteLog(logId);
    }
}
