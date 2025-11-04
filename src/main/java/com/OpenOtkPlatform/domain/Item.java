package com.OpenOtkPlatform.domain;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double price;
    
    @Column(name = "seller_id", nullable = false)
    private Long sellerId;
    
    @Column(nullable = false)
    private Integer stock;
    
    @Column(nullable = false)
    private Boolean available;
    
    @Column(name = "create_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
    
    @Column(name = "update_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;
    
    public Item() {
        this.stock = 0;
        this.available = true;
        this.createTime = new Date();
        this.updateTime = new Date();
    }
    
    public Item(String name, String description, Double price, Long sellerId, Integer stock) {
        this();
        this.name = name;
        this.description = description;
        this.price = price;
        this.sellerId = sellerId;
        this.stock = stock;
        this.available = stock > 0;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
        this.updateTime = new Date();
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
        this.updateTime = new Date();
    }
    
    public Double getPrice() {
        return price;
    }
    
    public void setPrice(Double price) {
        if (price >= 0) {
            this.price = price;
            this.updateTime = new Date();
        }
    }
    
    public Long getSellerId() {
        return sellerId;
    }
    
    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
        this.updateTime = new Date();
    }
    
    public Integer getStock() {
        return stock;
    }
    
    public void setStock(Integer stock) {
        if (stock >= 0) {
            this.stock = stock;
            this.available = stock > 0;
            this.updateTime = new Date();
        }
    }
    
    public Boolean getAvailable() {
        return available;
    }
    
    public void setAvailable(Boolean available) {
        this.available = available;
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
    
    public boolean isInStock() {
        return stock > 0 && available;
    }
    
    public boolean reduceStock(Integer quantity) {
        if (quantity > 0 && stock >= quantity) {
            stock -= quantity;
            available = stock > 0;
            updateTime = new Date();
            return true;
        }
        return false;
    }
    
    public void increaseStock(Integer quantity) {
        if (quantity > 0) {
            stock += quantity;
            available = true;
            updateTime = new Date();
        }
    }
    
    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", sellerId=" + sellerId +
                ", stock=" + stock +
                ", available=" + available +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
