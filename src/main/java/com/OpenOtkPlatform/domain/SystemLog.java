package com.OpenOtkPlatform.domain;

import java.util.Date;

/**
 * 系统日志实体类
 */
public class SystemLog {
    private Long id;
    private String operationType;
    private Long userId;
    private String description;
    private Date createTime;
    
    public SystemLog() {} // TODO - finish me
    public SystemLog(String operationType, Long userId, String description) {} // TODO - finish me
    
    public Long getId() { return null; } // TODO - finish me
    public void setId(Long id) {} // TODO - finish me
    
    public String getOperationType() { return null; } // TODO - finish me
    public void setOperationType(String operationType) {} // TODO - finish me
    
    public Long getUserId() { return null; } // TODO - finish me
    public void setUserId(Long userId) {} // TODO - finish me
    
    public String getDescription() { return null; } // TODO - finish me
    public void setDescription(String description) {} // TODO - finish me
    
    public Date getCreateTime() { return null; } // TODO - finish me
    public void setCreateTime(Date createTime) {} // TODO - finish me
}
