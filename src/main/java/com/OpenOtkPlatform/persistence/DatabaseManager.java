package com.OpenOtkPlatform.persistence;

import java.sql.*;
import java.util.Properties;

/**
 * 数据库管理器 - 单例模式
 */
public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;
    
    // MySQL数据库配置
    private static final String URL = "jdbc:mysql://localhost:3306/opentk_platform?useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "password";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    
    private DatabaseManager() {
        // 私有构造函数
    }
    
    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }
    
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            System.err.println("获取数据库连接失败: " + e.getMessage());
        }
        return connection;
    }
    
    public boolean connect() {
        try {
            Class.forName(DRIVER);
            
            Properties props = new Properties();
            props.setProperty("user", USERNAME);
            props.setProperty("password", PASSWORD);
            props.setProperty("useUnicode", "true");
            props.setProperty("characterEncoding", "UTF-8");
            
            connection = DriverManager.getConnection(URL, props);
            System.out.println("MySQL数据库连接成功");
            return true;
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL驱动未找到: " + e.getMessage());
            return false;
        } catch (SQLException e) {
            System.err.println("数据库连接失败: " + e.getMessage());
            return false;
        }
    }
    
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("数据库连接已关闭");
            }
        } catch (SQLException e) {
            System.err.println("关闭数据库连接失败: " + e.getMessage());
        }
    }
    
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    public boolean executeUpdate(String sql) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            int rows = stmt.executeUpdate(sql);
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("执行更新失败: " + e.getMessage());
            return false;
        }
    }
    
    public ResultSet executeQuery(String sql) {
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            System.err.println("执行查询失败: " + e.getMessage());
            return null;
        }
    }
    
    public void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // 创建数据库（如果不存在）
            stmt.execute("CREATE DATABASE IF NOT EXISTS opentk_platform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            stmt.execute("USE opentk_platform");
            
            // 创建用户表
            String createUserTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) UNIQUE NOT NULL, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "email VARCHAR(100) UNIQUE NOT NULL, " +
                    "phone VARCHAR(20) NOT NULL, " +
                    "balance DECIMAL(10,2) DEFAULT 0.00, " +
                    "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                    ")";
            stmt.execute(createUserTable);
            
            // 创建商品表
            String createItemTable = "CREATE TABLE IF NOT EXISTS items (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "description TEXT, " +
                    "price DECIMAL(10,2) NOT NULL, " +
                    "seller_id BIGINT NOT NULL, " +
                    "stock INTEGER DEFAULT 0, " +
                    "available BOOLEAN DEFAULT TRUE, " +
                    "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (seller_id) REFERENCES users(id)" +
                    ")";
            stmt.execute(createItemTable);
            
            // 创建订单表
            String createOrderTable = "CREATE TABLE IF NOT EXISTS orders (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "item_id BIGINT NOT NULL, " +
                    "buyer_id BIGINT NOT NULL, " +
                    "seller_id BIGINT NOT NULL, " +
                    "total_price DECIMAL(10,2) NOT NULL, " +
                    "status VARCHAR(20) DEFAULT 'PENDING', " +
                    "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (item_id) REFERENCES items(id), " +
                    "FOREIGN KEY (buyer_id) REFERENCES users(id), " +
                    "FOREIGN KEY (seller_id) REFERENCES users(id)" +
                    ")";
            stmt.execute(createOrderTable);
            
            // 创建系统日志表
            String createLogTable = "CREATE TABLE IF NOT EXISTS system_logs (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "operation_type VARCHAR(50) NOT NULL, " +
                    "user_id BIGINT, " +
                    "description TEXT NOT NULL, " +
                    "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            stmt.execute(createLogTable);
            
            System.out.println("数据库表初始化完成");
            
        } catch (SQLException e) {
            System.err.println("数据库初始化失败: " + e.getMessage());
        }
    }
}
