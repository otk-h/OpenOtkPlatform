package com.OpenOtkPlatform.domain;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "item_id", nullable = false)
    private Long itemId;
    
    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;
    
    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "quantity", nullable = false)
    private Long quantity;
    
    @Column(name = "total_price", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double totalPrice;
    
    @Column(nullable = false, length = 20)
    private String status;
    
    @Column(name = "create_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
    
    @Column(name = "update_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;
    
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_CONFIRMED = "CONFIRMED";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    
    public Order() {
        this.status = STATUS_PENDING;
        this.createTime = new Date();
        this.updateTime = new Date();
    }
    
    public Order(Long itemId, Long buyerId, Long sellerId, Long quantity, Double totalPrice) {
        this();
        this.itemId = itemId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.createTime = new Date();
        this.updateTime = new Date();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getItemId() {
        return itemId;
    }
    
    public void setItemId(Long itemId) {
        this.itemId = itemId;
        this.updateTime = new Date();
    }
    
    public Long getBuyerId() {
        return buyerId;
    }
    
    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
        this.updateTime = new Date();
    }
    
    public Long getSellerId() {
        return sellerId;
    }
    
    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
        this.updateTime = new Date();
    }
    
    public Long getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Long quantity) {
        if (quantity > 0) {
            this.quantity = quantity;
            this.updateTime = new Date();
        }
    }

    public Double getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(Double totalPrice) {
        if (totalPrice >= 0) {
            this.totalPrice = totalPrice;
            this.updateTime = new Date();
        }
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
        this.updateTime = new Date();
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
    public Date getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    
    public boolean confirmOrder() {
        if (STATUS_PENDING.equals(this.status)) {
            this.status = STATUS_CONFIRMED;
            this.updateTime = new Date();
            return true;
        }
        return false;
    }
    
    public boolean completeOrder() {
        if (STATUS_CONFIRMED.equals(this.status)) {
            this.status = STATUS_COMPLETED;
            this.updateTime = new Date();
            return true;
        }
        return false;
    }
    
    public boolean cancelOrder() {
        if (STATUS_PENDING.equals(this.status) || STATUS_CONFIRMED.equals(this.status)) {
            this.status = STATUS_CANCELLED;
            this.updateTime = new Date();
            return true;
        }
        return false;
    }
    
    public boolean isPending() {
        return STATUS_PENDING.equals(this.status);
    }
    
    public boolean isConfirmed() {
        return STATUS_CONFIRMED.equals(this.status);
    }
    
    public boolean isCompleted() {
        return STATUS_COMPLETED.equals(this.status);
    }
    
    public boolean isCancelled() {
        return STATUS_CANCELLED.equals(this.status);
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", itemId=" + itemId +
                ", buyerId=" + buyerId +
                ", sellerId=" + sellerId +
                ", totalPrice=" + totalPrice +
                ", status='" + status + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
