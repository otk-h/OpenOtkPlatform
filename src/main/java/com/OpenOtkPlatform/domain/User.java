package com.OpenOtkPlatform.domain;

import java.util.Date;

/**
 * 用户实体类
 */
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private Double balance;
    private Date createTime;
    private Date updateTime;
    
    public User() {
        this.balance = 0.0;
        this.createTime = new Date();
        this.updateTime = new Date();
    }
    
    public User(String username, String password, String email, String phone) {
        this();
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.createTime = new Date();
        this.updateTime = new Date();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
        this.updateTime = new Date();
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
        this.updateTime = new Date();
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
        this.updateTime = new Date();
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
        this.updateTime = new Date();
    }
    
    public Double getBalance() {
        return balance;
    }
    
    public void setBalance(Double balance) {
        this.balance = balance;
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
    
    public void addBalance(Double amount) {
        if (amount > 0) {
            this.balance += amount;
            this.updateTime = new Date();
        }
    }
    
    public boolean deductBalance(Double amount) {
        if (amount > 0 && this.balance >= amount) {
            this.balance -= amount;
            this.updateTime = new Date();
            return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", balance=" + balance +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
