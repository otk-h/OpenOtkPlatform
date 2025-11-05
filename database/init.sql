-- 数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS platform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE platform;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    balance DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
);

-- 商品表
CREATE TABLE IF NOT EXISTS items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    seller_id BIGINT NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_seller_id (seller_id),
    INDEX idx_status (status),
    INDEX idx_name (name),
    FOREIGN KEY (seller_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL,
    buyer_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_buyer_id (buyer_id),
    INDEX idx_seller_id (seller_id),
    INDEX idx_status (status),
    INDEX idx_item_id (item_id),
    FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE,
    FOREIGN KEY (buyer_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (seller_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 系统日志表
CREATE TABLE IF NOT EXISTS system_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    operation_type VARCHAR(50) NOT NULL,
    user_id BIGINT,
    description TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_operation_type (operation_type),
    INDEX idx_create_time (create_time),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 插入初始测试数据

-- 插入测试用户
INSERT INTO users (username, password, email, phone, balance) VALUES
('admin', 'admin.password', 'admin@example.com', '01234567890', 1000.00),
('user1', 'user1.password', 'user1@example.com', '01234567890', 500.00),
('user2', 'user2.password', 'user2@example.com', '01234567890', 200.00);

-- 插入测试商品
INSERT INTO items (name, description, price, seller_id, stock) VALUES
('example item 1', 'example desciption 1', 7999.00, 3, 10),
('example item 2', 'example desciption 2', 8999.00, 3, 5),
('example item 3', 'example desciption 3', 7999.00, 3, 10),
('example item 4', 'example desciption 4', 8999.00, 3, 5);

-- 插入测试订单
INSERT INTO orders (item_id, buyer_id, seller_id, total_price, status) VALUES
(1, 2, 3, 7999.00, 'COMPLETED'),
(3, 2, 3, 1899.00, 'CONFIRMED'),
(4, 2, 3, 2999.00, 'PENDING');

-- 插入系统日志示例
INSERT INTO system_logs (operation_type, user_id, description) VALUES
('REGISTER', 1, 'User registered account'),
('LOGIN', 1, 'User logged in'),
('PUBLISH_ITEM', 3, 'User published item, Item ID: 1'),
('PUBLISH_ITEM', 3, 'User published item, Item ID: 3'),
('CREATE_ORDER', 2, 'User created order, Order ID: 1'),
('COMPLETE_ORDER', 2, 'User completed order, Order ID: 1');

-- 创建视图：用户订单统计
CREATE VIEW user_order_stats AS
SELECT 
    u.id AS user_id,
    u.username,
    COUNT(DISTINCT o.id) AS total_orders,
    COUNT(DISTINCT CASE WHEN o.status = 'COMPLETED' THEN o.id END) AS completed_orders,
    COUNT(DISTINCT CASE WHEN o.status = 'PENDING' THEN o.id END) AS pending_orders,
    SUM(CASE WHEN o.status = 'COMPLETED' THEN o.total_price ELSE 0 END) AS total_spent
FROM users u
LEFT JOIN orders o ON u.id = o.buyer_id
GROUP BY u.id, u.username;

-- 创建视图：商品销售统计
CREATE VIEW item_sales_stats AS
SELECT 
    i.id AS item_id,
    i.name AS item_name,
    i.price,
    COUNT(DISTINCT o.id) AS total_orders,
    COUNT(DISTINCT CASE WHEN o.status = 'COMPLETED' THEN o.id END) AS completed_orders,
    SUM(CASE WHEN o.status = 'COMPLETED' THEN o.total_price ELSE 0 END) AS total_revenue
FROM items i
LEFT JOIN orders o ON i.id = o.item_id
GROUP BY i.id, i.name, i.price;

-- 显示表结构信息
SHOW TABLES;

-- 显示各表记录数
SELECT 
    'users' AS table_name, 
    COUNT(*) AS record_count 
FROM users
UNION ALL
SELECT 
    'items' AS table_name, 
    COUNT(*) AS record_count 
FROM items
UNION ALL
SELECT 
    'orders' AS table_name, 
    COUNT(*) AS record_count 
FROM orders
UNION ALL
SELECT 
    'system_logs' AS table_name, 
    COUNT(*) AS record_count 
FROM system_logs;
