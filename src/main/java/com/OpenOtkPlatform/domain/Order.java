package com.OpenOtkPlatform.domain;

import java.util.Date;

/**
 * 订单实体类
 */
public class Order {
    private Long id;
    private Long itemId;
    private Long buyerId;
    private Long sellerId;
    private Double totalPrice;
    private String status;
    private Date createTime;
    
    public Order() {} // TODO - finish me
    public Order(Long itemId, Long buyerId, Long sellerId, Double totalPrice) {} // TODO - finish me
    
    public Long getId() { return null; } // TODO - finish me
    public void setId(Long id) {} // TODO - finish me
    
    public Long getItemId() { return null; } // TODO - finish me
    public void setItemId(Long itemId) {} // TODO - finish me
    
    public Long getBuyerId() { return null; } // TODO - finish me
    public void setBuyerId(Long buyerId) {} // TODO - finish me
    
    public Long getSellerId() { return null; } // TODO - finish me
    public void setSellerId(Long sellerId) {} // TODO - finish me
    
    public Double getTotalPrice() { return null; } // TODO - finish me
    public void setTotalPrice(Double totalPrice) {} // TODO - finish me
    
    public String getStatus() { return null; } // TODO - finish me
    public void setStatus(String status) {} // TODO - finish me
    
    public Date getCreateTime() { return null; } // TODO - finish me
    public void setCreateTime(Date createTime) {} // TODO - finish me
}
