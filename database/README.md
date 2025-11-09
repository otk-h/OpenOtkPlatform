# OpenOtkPlatform 数据库文档

## 数据库概述

OpenOtkPlatform 使用 MySQL 8.0 作为关系型数据库，采用 UTF-8 字符集，支持完整的交易平台功能。

## 数据库配置

### 连接配置
- **数据库名称**: `platform`
- **字符集**: `utf8mb4`
- **排序规则**: `utf8mb4_unicode_ci`
- **连接URL**: `jdbc:mysql://localhost:3306/platform`
- **驱动**: `com.mysql.cj.jdbc.Driver`

### SpringBoot 配置
在 `application.properties` 中配置数据库连接：

```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/platform?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=OTAKU
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA配置
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

## 数据库表结构

### 1. users 表（用户表）
| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 用户ID | 主键，自增 |
| username | VARCHAR(50) | 用户名 | 唯一，非空 |
| password | VARCHAR(255) | 密码 | 非空 |
| email | VARCHAR(100) | 邮箱 | 唯一，非空 |
| phone | VARCHAR(20) | 手机号 | 非空 |
| balance | DECIMAL(10,2) | 账户余额 | 默认0.00 |
| create_time | TIMESTAMP | 创建时间 | 默认当前时间 |
| update_time | TIMESTAMP | 更新时间 | 自动更新 |

**索引**:
- `idx_username` (username)
- `idx_email` (email)

### 2. items 表（商品表）
| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 商品ID | 主键，自增 |
| name | VARCHAR(100) | 商品名称 | 非空 |
| description | TEXT | 商品描述 |  |
| price | DECIMAL(10,2) | 商品价格 | 非空 |
| seller_id | BIGINT | 卖家ID | 外键，非空 |
| stock | INT | 库存数量 | 默认0 |
| status | VARCHAR(20) | 商品状态 | 默认'AVAILABLE' |
| create_time | TIMESTAMP | 创建时间 | 默认当前时间 |
| update_time | TIMESTAMP | 更新时间 | 自动更新 |

**索引**:
- `idx_seller_id` (seller_id)
- `idx_status` (status)
- `idx_name` (name)

**外键约束**:
- `seller_id` 引用 `users(id)` ON DELETE CASCADE

### 3. orders 表（订单表）
| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 订单ID | 主键，自增 |
| item_id | BIGINT | 商品ID | 外键，非空 |
| buyer_id | BIGINT | 买家ID | 外键，非空 |
| seller_id | BIGINT | 卖家ID | 外键，非空 |
| quantity | BIGINT | 购买数量 | 非空 |
| total_price | DECIMAL(10,2) | 订单总价 | 非空 |
| status | VARCHAR(20) | 订单状态 | 默认'PENDING' |
| create_time | TIMESTAMP | 创建时间 | 默认当前时间 |
| update_time | TIMESTAMP | 更新时间 | 自动更新 |

**索引**:
- `idx_buyer_id` (buyer_id)
- `idx_seller_id` (seller_id)
- `idx_status` (status)
- `idx_item_id` (item_id)

**外键约束**:
- `item_id` 引用 `items(id)` ON DELETE CASCADE
- `buyer_id` 引用 `users(id)` ON DELETE CASCADE
- `seller_id` 引用 `users(id)` ON DELETE CASCADE

### 4. system_logs 表（系统日志表）
| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 日志ID | 主键，自增 |
| operation_type | VARCHAR(50) | 操作类型 | 非空 |
| user_id | BIGINT | 用户ID | 可为空 |
| description | TEXT | 操作描述 | 非空 |
| create_time | TIMESTAMP | 创建时间 | 默认当前时间 |

**索引**:
- `idx_user_id` (user_id)
- `idx_operation_type` (operation_type)
- `idx_create_time` (create_time)

**外键约束**:
- `user_id` 引用 `users(id)` ON DELETE SET NULL

## 数据库视图

### 1. user_order_stats（用户订单统计视图）
统计每个用户的订单情况：
- 总订单数
- 已完成订单数
- 待处理订单数
- 总消费金额

### 2. item_sales_stats（商品销售统计视图）
统计每个商品的销售情况：
- 总订单数
- 已完成订单数
- 总销售额

## 初始化数据

### 测试用户
- **admin** (ID: 1) - 管理员用户，余额1000元
- **buyer1** (ID: 2) - 买家用户，余额500元
- **seller1** (ID: 3) - 卖家用户，余额200元
- **seller2** (ID: 4) - 卖家用户，余额300元

### 测试商品
- iPhone 15 Pro - 7999元，库存10
- MacBook Air M2 - 8999元，库存5
- AirPods Pro 2 - 1899元，库存20
- iPad Air 5 - 4399元，库存8
- Apple Watch Series 9 - 2999元，库存15

### 测试订单
- 已完成订单：iPhone 15 Pro
- 已确认订单：AirPods Pro 2
- 待处理订单：Apple Watch Series 9

## 数据库操作指南

### 初始化数据库
```bash
# 登录MySQL
mysql -u root -p

# 执行初始化脚本
source database/init.sql
```

### 备份数据库
```bash
# 执行备份脚本
source database/backup.sql
```

### 常用查询语句

#### 查看用户订单
```sql
SELECT u.username, o.id, o.total_price, o.status, o.create_time
FROM users u
JOIN orders o ON u.id = o.buyer_id
WHERE u.id = 2;
```

#### 查看商品销售统计
```sql
SELECT * FROM item_sales_stats;
```

#### 查看用户订单统计
```sql
SELECT * FROM user_order_stats;
```

#### 查看系统日志
```sql
SELECT u.username, sl.operation_type, sl.description, sl.create_time
FROM system_logs sl
LEFT JOIN users u ON sl.user_id = u.id
ORDER BY sl.create_time DESC
LIMIT 10;
```

## 维护建议

### 定期维护
1. **备份数据库**: 每周执行一次完整备份
2. **清理日志**: 每月清理超过6个月的系统日志
3. **优化索引**: 定期检查索引使用情况

### 性能优化
1. 监控慢查询日志
2. 定期分析表统计信息
3. 根据业务需求调整连接池参数

### 安全建议
1. 定期更改数据库密码
2. 限制数据库访问IP
3. 启用SSL连接
4. 定期审计数据库权限

## 故障排除

### 常见问题

1. **连接失败**
   - 检查MySQL服务是否启动
   - 验证用户名和密码
   - 检查防火墙设置

2. **字符集问题**
   - 确保使用utf8mb4字符集
   - 检查表字段的字符集设置

3. **外键约束错误**
   - 检查关联数据是否存在
   - 验证外键约束设置

### 日志分析
查看MySQL错误日志和慢查询日志，定位性能问题和错误原因。
