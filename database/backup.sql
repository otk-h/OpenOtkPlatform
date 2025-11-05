-- 数据库备份脚本
-- 在MySQL命令行中执行此脚本可以备份数据库结构和数据

-- 设置字符集
SET NAMES utf8mb4;

-- 备份数据库结构
-- 用户表
CREATE TABLE IF NOT EXISTS users_backup (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    balance DECIMAL(10,2) DEFAULT 0.00,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
);

-- 商品表
CREATE TABLE IF NOT EXISTS items_backup (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    seller_id BIGINT NOT NULL,
    stock INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'AVAILABLE',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_seller_id (seller_id),
    INDEX idx_status (status),
    INDEX idx_name (name)
);

-- 订单表
CREATE TABLE IF NOT EXISTS orders_backup (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL,
    buyer_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_buyer_id (buyer_id),
    INDEX idx_seller_id (seller_id),
    INDEX idx_status (status),
    INDEX idx_item_id (item_id)
);

-- 系统日志表
CREATE TABLE IF NOT EXISTS system_logs_backup (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    operation_type VARCHAR(50) NOT NULL,
    user_id BIGINT,
    description TEXT NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_operation_type (operation_type),
    INDEX idx_create_time (create_time)
);

-- 备份数据
-- 备份用户数据
INSERT INTO users_backup (id, username, password, email, phone, balance, create_time, update_time)
SELECT id, username, password, email, phone, balance, create_time, update_time
FROM users;

-- 备份商品数据
INSERT INTO items_backup (id, name, description, price, seller_id, stock, status, create_time, update_time)
SELECT id, name, description, price, seller_id, stock, status, create_time, update_time
FROM items;

-- 备份订单数据
INSERT INTO orders_backup (id, item_id, buyer_id, seller_id, total_price, status, create_time, update_time)
SELECT id, item_id, buyer_id, seller_id, total_price, status, create_time, update_time
FROM orders;

-- 备份系统日志数据
INSERT INTO system_logs_backup (id, operation_type, user_id, description, create_time)
SELECT id, operation_type, user_id, description, create_time
FROM system_logs;

-- 显示备份完成信息
SELECT 'Backup completed successfully!' AS message;

-- 显示备份数据统计
SELECT 
    'users_backup' AS table_name, 
    COUNT(*) AS record_count 
FROM users_backup
UNION ALL
SELECT 
    'items_backup' AS table_name, 
    COUNT(*) AS record_count 
FROM items_backup
UNION ALL
SELECT 
    'orders_backup' AS table_name, 
    COUNT(*) AS record_count 
FROM orders_backup
UNION ALL
SELECT 
    'system_logs_backup' AS table_name, 
    COUNT(*) AS record_count 
FROM system_logs_backup;
