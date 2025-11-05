package com.OpenOtkPlatform.service;

import com.OpenOtkPlatform.domain.SystemLog;
import com.OpenOtkPlatform.repository.SystemLogRepository;
import com.OpenOtkPlatform.util.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LogService {

    @Autowired
    private SystemLogRepository systemLogRepository;

    private Logger logger = Logger.getInstance();

    private void logUserOperation(String operationType, Long userId, String description) {
        if (operationType == null || operationType.trim().isEmpty()
            || (userId != null && userId <= 0)
            || description == null || description.trim().isEmpty()) {
            return;
        }

        SystemLog log = new SystemLog(operationType, userId, description);
        systemLogRepository.save(log);

        logger.info(String.format("logUserOperation: [%s], userId: %d, %s",
                operationType, userId, description));
    }

    // private void logSystemEvent(String operationType, String description) {
    //     if (operationType == null || operationType.trim().isEmpty() || 
    //         description == null || description.trim().isEmpty()) {
    //         return;
    //     }

    //     SystemLog log = new SystemLog(operationType, null, description);
    //     systemLogRepository.save(log);

    //     logger.info(String.format("logSystemEvent: [%s], description: %s", operationType, description));
    // }

    public void logRegister(Long userId) {
        logUserOperation(SystemLog.OPERATION_REGISTER, userId, "description: null");
    }

    
    public void logLogin(Long userId) {
        logUserOperation(SystemLog.OPERATION_LOGIN, userId, "description: null");
    }

    public void logLogout(Long userId) {
        logUserOperation(SystemLog.OPERATION_LOGOUT, userId, "description: null");
    }

    public void logUserDelete(Long userId) {
        logUserOperation(SystemLog.OPERATION_DELETE_USER, userId, "description: null");
    }

    public void logUserUpdate(Long userId, String email, String phone) {
        logUserOperation(SystemLog.OPERATION_UPDATE_USER, userId, String.format("email: %s, phone: %s", email, phone));
    }

    public void logGetUserInfo(Long userId) {
        logUserOperation(SystemLog.OPERATION_GET_USER_INFO, userId, "description: null");
    }

    public void logResetPassword(Long userId, String oldPassword, String newPassword) {
        logUserOperation(SystemLog.OPERATION_LOGOUT, userId,
                String.format("oldPassword: %s, newPassword: %s", oldPassword, newPassword));
    }

    public void logItemPublish(Long userId, Long itemId) {
        logUserOperation(SystemLog.OPERATION_PUBLISH_ITEM, userId,
                String.format("itemId: %d", itemId));
    }

    public void logItemUpdate(Long userId, Long itemId) {
        logUserOperation(SystemLog.OPERATION_UPDATE_ITEM, userId,
                String.format("itemId: %d", itemId));
    }

    public void logItemDelete(Long userId, Long itemId) {
        logUserOperation(SystemLog.OPERATION_DELETE_ITEM, userId,
                String.format("itemId: %d", itemId));
    }

    public void logReduceStock(Long userId, Long itemId, Integer quantity) {
        logUserOperation(SystemLog.OPERATION_REDUCE_STOCK, userId,
                String.format("itemId: %d, quantity: %d", itemId, quantity));
    }

    public void logOrderCreate(Long userId, Long orderId) {
        logUserOperation(SystemLog.OPERATION_CREATE_ORDER, userId,
                String.format("orderId: %d", orderId));
    }

    public void logOrderConfirm(Long userId, Long orderId) {
        logUserOperation(SystemLog.OPERATION_CONFIRM_ORDER, userId,
                String.format("orderId: %d", orderId));
    }

    public void logOrderUpdate(Long userId, Long orderId, String status) {
        logUserOperation(SystemLog.OPERATION_UPDATE_ORDER, userId,
                String.format("orderId: %d, status: %s", orderId, status));
    }

    public void logOrderCancel(Long userId, Long orderId) {
        logUserOperation(SystemLog.OPERATION_CREATE_ORDER, userId,
                String.format("orderId: %d", orderId));
    }

    public void logOrderComplete(Long userId, Long orderId) {
        logUserOperation(SystemLog.OPERATION_CREATE_ORDER, userId,
                String.format("orderId: %d", orderId));
    }

    public void logExchangeInfo(Long userId1, Long userId2) {
        logUserOperation(SystemLog.OPERATION_CREATE_ORDER, userId1,
                String.format("userId: %d", userId2));
    }

    public void logRecharge(Long userId, Double amount) {
        logUserOperation(SystemLog.OPERATION_RECHARGE, userId,
                String.format("amount: %f", amount));
    }

    public void logGetBalance(Long userId) {
        logUserOperation(SystemLog.OPERATION_RECHARGE, userId, "description: null");
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
