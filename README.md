# OpenOtkPlatform 网上购物系统

一个基于SpringBoot的现代化网上购物平台，实现了完整的RESTful API和前后端分离架构。

## 功能特性

- ✅ 用户注册登录和认证
- ✅ 商品发布、搜索和购买
- ✅ 订单管理和状态跟踪
- ✅ 系统日志记录和操作审计
- ✅ 联系方式安全交换
- ✅ 余额管理和充值功能
- ✅ 完整的RESTful API设计
- ✅ 前后端分离架构

## 技术架构

### 后端技术栈
- **Spring Boot 3.5.7** - 现代化Java应用框架
- **Spring Data JPA** - 数据持久化层
- **MySQL 8.0** - 关系型数据库
- **Spring Web** - RESTful API支持
- **Spring Security** - 安全认证（预留）
- **HikariCP** - 数据库连接池

### 设计模式
- **单例模式**: Logger工具类
- **观察者模式**: 日志系统
- **策略模式**: 验证和加密
- **MVC模式**: 整体架构
- **Repository模式**: 数据访问层

### 项目结构
```
src/main/java/com/OpenOtkPlatform/
├── PlatformApplication.java      # SpringBoot主应用类
├── domain/                       # JPA实体类
│   ├── User.java                 # 用户实体
│   ├── Item.java                 # 商品实体
│   ├── Order.java                # 订单实体
│   └── SystemLog.java            # 系统日志实体
├── repository/                   # 数据访问层
│   ├── UserRepository.java       # 用户数据访问接口
│   ├── ItemRepository.java       # 商品数据访问接口
│   ├── OrderRepository.java      # 订单数据访问接口
│   └── SystemLogRepository.java  # 系统日志数据访问接口
├── service/                      # 业务逻辑层
│   ├── UserService.java          # 用户服务
│   ├── ItemService.java          # 商品服务
│   ├── OrderService.java         # 订单服务
│   └── LogService.java           # 日志服务
├── api/                          # REST控制器层
│   ├── AuthController.java       # 认证控制器
│   ├── UserController.java       # 用户控制器
│   ├── ItemController.java       # 商品控制器
│   └── OrderController.java      # 订单控制器
└── util/                         # 工具类
    ├── PasswordUtil.java         # 密码工具
    ├── ValidationUtil.java       # 验证工具
    └── Logger.java               # 日志记录器
```

### 前端技术
- **HTML5/CSS3** - 页面结构和样式
- **JavaScript (ES6+)** - 前端交互逻辑
- **Fetch API** - HTTP请求处理
- **响应式设计** - 移动端适配

## 构建和运行

### 环境要求
- Java 21+
- Maven 3.6+
- MySQL 8.0+
- Node.js (可选，用于前端开发)

### 数据库配置

1. 创建MySQL数据库：
```sql
CREATE DATABASE openotk_platform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 配置数据库连接（application.properties）：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/openotk_platform
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 构建项目
```bash
# 克隆项目
git clone <repository-url>
cd OpenOtkPlatform

# 使用Maven构建
mvn clean compile

# 打包为可执行JAR
mvn clean package
```

### 运行项目
```bash
# 运行SpringBoot应用
mvn spring-boot:run

# 或者运行打包后的JAR
java -jar target/OpenOtkPlatform-1.0.0.jar
```

### 开发模式
```bash
# 编译项目
mvn compile

# 运行测试
mvn test

# 清理项目
mvn clean
```

## API接口文档

### 认证接口 (AuthController)
- `POST /api/auth/signup` - 用户注册
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户登出
- `POST /api/auth/change-password` - 修改密码

### 用户接口 (UserController)
- `GET /api/users/{id}` - 获取用户信息
- `PUT /api/users/{id}` - 更新用户信息
- `POST /api/users/{id}/recharge` - 充值余额
- `GET /api/users/{id}/balance` - 获取用户余额
- `DELETE /api/users/{id}` - 删除用户

### 商品接口 (ItemController)
- `POST /api/items` - 发布商品
- `GET /api/items` - 获取所有商品
- `GET /api/items/{id}` - 获取商品详情
- `GET /api/items/search` - 搜索商品
- `PUT /api/items/{id}` - 更新商品
- `DELETE /api/items/{id}` - 删除商品

### 订单接口 (OrderController)
- `POST /api/orders` - 创建订单
- `GET /api/orders` - 获取所有订单
- `GET /api/orders/{id}` - 获取订单详情
- `GET /api/orders/buyer/{buyerId}` - 获取买家订单
- `GET /api/orders/seller/{sellerId}` - 获取卖家订单
- `PUT /api/orders/{id}/status` - 更新订单状态
- `POST /api/orders/{id}/cancel` - 取消订单
- `POST /api/orders/{id}/complete` - 完成订单
- `GET /api/orders/{id}/contact` - 交换联系方式

## 数据库设计

系统使用MySQL数据库，包含以下表：

### users表
- `id` - 主键，自增
- `username` - 用户名，唯一
- `password` - 加密密码
- `email` - 邮箱，唯一
- `phone` - 手机号
- `balance` - 账户余额
- `create_time` - 创建时间
- `update_time` - 更新时间

### items表
- `id` - 主键，自增
- `name` - 商品名称
- `description` - 商品描述
- `price` - 商品价格
- `seller_id` - 卖家ID
- `stock` - 库存数量
- `status` - 商品状态
- `create_time` - 创建时间
- `update_time` - 更新时间

### orders表
- `id` - 主键，自增
- `item_id` - 商品ID
- `buyer_id` - 买家ID
- `seller_id` - 卖家ID
- `total_price` - 订单总价
- `status` - 订单状态
- `create_time` - 创建时间
- `update_time` - 更新时间

### system_logs表
- `id` - 主键，自增
- `operation_type` - 操作类型
- `user_id` - 用户ID
- `description` - 操作描述
- `create_time` - 创建时间

## 前端使用

1. 启动后端服务：
```bash
mvn spring-boot:run
```

2. 在浏览器中打开 `web/index.html`

3. 主要功能页面：
   - `index.html` - 首页和商品浏览
   - `user.html` - 用户管理
   - `product.html` - 商品详情

## 开发说明

### 代码规范
- 遵循Java命名规范
- 使用SpringBoot最佳实践
- RESTful API设计规范
- 统一的异常处理

### 安全考虑
- 密码加密存储
- 输入数据验证
- SQL注入防护
- XSS攻击防护

## 许可证

MIT License
