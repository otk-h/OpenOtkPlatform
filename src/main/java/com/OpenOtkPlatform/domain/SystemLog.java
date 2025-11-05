package com.OpenOtkPlatform.domain;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "system_logs")
public class SystemLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "operation_type", nullable = false, length = 50)
    private String operationType;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "create_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
    
    public static final String OPERATION_REGISTER = "REGISTER";
    public static final String OPERATION_LOGIN = "LOGIN";
    public static final String OPERATION_LOGOUT = "LOGOUT";
    public static final String OPERATION_DELETE_USER = "DELETE_USER";
    public static final String OPERATION_UPDATE_USER = "UPDATE_USER";
    public static final String OPERATION_GET_USER_INFO = "GET_USER_INFO";
    public static final String OPERATION_RESET_PASSWORD = "RESET_PASSWORD";
    public static final String OPERATION_PUBLISH_ITEM = "PUBLISH_ITEM";
    public static final String OPERATION_UPDATE_ITEM = "UPDATE_ITEM";
    public static final String OPERATION_DELETE_ITEM = "DELETE_ITEM";
    public static final String OPERATION_REDUCE_STOCK = "REDUCE_STOCK";
    public static final String OPERATION_CREATE_ORDER = "CREATE_ORDER";
    public static final String OPERATION_UPDATE_ORDER = "UPDATE_ORDER";
    public static final String OPERATION_CONFIRM_ORDER = "CONFIRM_ORDER";
    public static final String OPERATION_COMPLETE_ORDER = "COMPLETE_ORDER";
    public static final String OPERATION_EXCHANGE_INFO = "EXCHANGE_INFO";
    public static final String OPERATION_CANCEL_ORDER = "CANCEL_ORDER";
    public static final String OPERATION_RECHARGE = "RECHARGE";
    public static final String OPERATION_DEDUCT_BALANCE = "DEDUCT_BALANCE";
    public static final String OPERATION_GET_BALANCE = "GET_BALANCE";
    
    public SystemLog() {
        this.createTime = new Date();
    }
    
    public SystemLog(String operationType, Long userId, String description) {
        this();
        this.operationType = operationType;
        this.userId = userId;
        this.description = description;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getOperationType() {
        return operationType;
    }
    
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
    public boolean isUserOperation() {
        return userId != null;
    }
    
    public boolean isSystemOperation() {
        return userId == null;
    }
    
    public String getLogLevel() {
        if (OPERATION_LOGIN.equals(operationType)
            || OPERATION_REGISTER.equals(operationType)
            || OPERATION_LOGOUT.equals(operationType)
        ) {
            return "INFO";
        } else if (OPERATION_CANCEL_ORDER.equals(operationType)) {
            return "WARN";
        } else {
            return "INFO";
        }
    }
    
    @Override
    public String toString() {
        return "SystemLog{" +
                "id=" + id +
                ", operationType='" + operationType + '\'' +
                ", userId=" + userId +
                ", description='" + description + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
