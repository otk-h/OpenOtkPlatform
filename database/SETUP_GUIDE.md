# OpenOtkPlatform 数据库设置指南

## 问题诊断

根据启动日志，当前遇到的问题是：**"Communications link failure - Connection refused"**

这表明应用程序无法连接到MySQL数据库服务器。可能的原因包括：

1. MySQL服务未启动
2. 数据库不存在
3. 连接参数配置错误
4. 防火墙阻止连接

## 解决方案

### 步骤1：启动MySQL服务

#### Windows系统
```cmd
# 启动MySQL服务
net start mysql
```

#### macOS系统
```bash
# 使用Homebrew安装的MySQL
brew services start mysql

# 或者直接启动
mysql.server start
```

#### Linux系统
```bash
# Ubuntu/Debian
sudo systemctl start mysql

# CentOS/RHEL
sudo systemctl start mysqld
```

### 步骤2：创建数据库

登录MySQL并创建数据库：

```sql
mysql -u root -p

CREATE DATABASE IF NOT EXISTS platform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

SHOW DATABASES;

EXIT;
```

### 步骤3：执行初始化脚本

```sql
mysql -u root -p

USE platform;

source database/init.sql;

-- 验证数据导入
SELECT * FROM users;
SELECT * FROM items;
SELECT * FROM orders;
SELECT * FROM system_logs;
```

### 步骤4：验证数据库连接

使用命令行测试连接：

```bash
mysql -u root -p -h localhost -P 3306 platform
```

### 步骤5：配置应用程序

确保 `src/main/resources/application.properties` 中的配置正确：

```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/platform?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8&allowPublicKeyRetrieval=true
spring.datasource.username=database_username
spring.datasource.password=database_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

## 故障排除

### 常见问题及解决方案

#### 1. "Access denied for user 'root'@'localhost'"
**解决方案**：
```sql
-- 重置root密码
ALTER USER 'root'@'localhost' IDENTIFIED BY 'new_password';
FLUSH PRIVILEGES;
```

#### 2. "Unknown database 'platform'"
**解决方案**：
```sql
CREATE DATABASE platform;
```

#### 3. MySQL服务无法启动
**解决方案**：
- 检查MySQL日志文件
- 确保端口3306未被占用
- 检查磁盘空间

#### 4. 连接超时
**解决方案**：
```properties
# 在application.properties中添加连接超时设置
spring.datasource.hikari.connection-timeout=60000
spring.datasource.hikari.validation-timeout=3000
spring.datasource.hikari.login-timeout=5
```

## 生产环境配置

### 安全建议
1. 使用专用数据库用户，而非root
2. 启用SSL连接
3. 定期更改密码
4. 配置数据库备份

### 性能优化
```properties
# 生产环境数据库配置
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=10
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1200000
```

## 验证成功

当数据库配置成功后，启动应用应该看到类似以下日志：

```
2025-11-05T23:14:01.236+08:00  INFO - Starting PlatformApplication
2025-11-05T23:14:02.018+08:00  INFO - Finished Spring Data repository scanning
2025-11-05T23:14:03.264+08:00  INFO - HikariPool-1 - Starting...
2025-11-05T23:14:03.456+08:00  INFO - HikariPool-1 - Start completed.
2025-11-05T23:14:04.235+08:00  INFO - Tomcat started on port 8080
```

如果看到这些日志，说明数据库连接成功，应用已正常启动！
