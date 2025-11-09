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
    
    public static final String AUTH_OPERATION_REGISTER = "AUTH_REGISTER";
    public static final String AUTH_OPERATION_LOGIN =    "AUTH_LOGIN";
    public static final String AUTH_OPERATION_LOGOUT =   "AUTH_LOGOUT";
    public static final String ITEM_OPERATION_PUBLISH =  "ITEM_PUBLISH";
    public static final String ITEM_OPERATION_UPDATE =   "ITEM_UPDATE";
    public static final String ORDER_OPERATION_CREATE =  "ORDER_CREATE";
    public static final String ORDER_OPERATION_CONFIRM = "ORDER_CONFIRM";
    public static final String ORDER_OPERATION_COMPLETE ="ORDER_COMPLETE";
    public static final String ORDER_OPERATION_CANCEL =  "ORDER_CANCEL";
    public static final String USER_OPERATION_UPDATE =   "USER_UPDATE";
    
    // public static final String ORDER_OPERATION_UPDATE =  "ORDER_UPDATE";
    // public static final String OPERATION_DELETE_USER = "DELETE_USER";

    // public static final String OPERATION_GET_USER_INFO = "GET_USER_INFO";
    // public static final String OPERATION_RESET_PASSWORD = "RESET_PASSWORD";

    // public static final String OPERATION_DELETE_ITEM = "DELETE_ITEM";
    
    // public static final String OPERATION_EXCHANGE_INFO = "EXCHANGE_INFO";
    // public static final String OPERATION_RECHARGE = "RECHARGE";
    // public static final String OPERATION_DEDUCT_BALANCE = "DEDUCT_BALANCE";
    // public static final String OPERATION_GET_BALANCE = "GET_BALANCE";
    
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
        if (AUTH_OPERATION_LOGIN.equals(operationType)
            || AUTH_OPERATION_REGISTER.equals(operationType)
            || AUTH_OPERATION_LOGOUT.equals(operationType)
        ) {
            return "INFO";
        // } else if (ORDER_OPERATION_CANCEL.equals(operationType)) {
        //     return "WARN";
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
