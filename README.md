# OpenOtkPlatform 网上购物系统

一个简单的网上购物系统Java代码框架，实现了完整的MVC架构和必要的设计模式。

## 功能特性

- ✅ 用户注册登录
- ✅ 商品发布和购买
- ✅ 订单管理
- ✅ 系统日志记录
- ✅ 联系方式交换
- ✅ 完整的MVC架构
- ✅ 多种设计模式应用

## 技术架构

### 设计模式
- **单例模式**: UserService, ItemService, OrderService, LogService, DatabaseManager
- **工厂模式**: 对象创建
- **观察者模式**: 日志系统
- **策略模式**: 验证和加密
- **MVC模式**: 整体架构

### 项目结构
```
src/main/java/com/OpenOtkPlatform/
├── PlatformApplication.java      # 主应用类
├── domain/                       # 实体类
│   ├── User.java                 # 用户实体
│   ├── Item.java                 # 商品实体
│   ├── Order.java                # 订单实体
│   └── SystemLog.java            # 系统日志实体
├── service/                      # 业务逻辑层
│   ├── UserService.java          # 用户服务
│   ├── ItemService.java          # 商品服务
│   ├── OrderService.java         # 订单服务
│   └── LogService.java           # 日志服务
├── api/                          # 控制器层
│   ├── AuthController.java       # 认证控制器
│   ├── UserController.java       # 用户控制器
│   ├── ItemController.java       # 商品控制器
│   └── OrderController.java      # 订单控制器
├── persistence/                  # 持久化层
│   ├── DatabaseManager.java      # 数据库管理器
│   ├── UserDAO.java              # 用户数据访问
│   ├── ItemDAO.java              # 商品数据访问
│   ├── OrderDAO.java             # 订单数据访问
│   └── LogDAO.java               # 日志数据访问
└── util/                         # 工具类
    ├── PasswordUtil.java         # 密码工具
    ├── ValidationUtil.java       # 验证工具
    └── Logger.java               # 日志记录器
```

## 构建和运行

### 环境要求
- Java 8+
- Maven 3.6+

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
# 运行主应用
java -jar target/OpenOtkPlatform-1.0.0.jar

# 或者直接运行
mvn exec:java -Dexec.mainClass="com.OpenOtkPlatform.PlatformApplication"
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

## API接口说明

### 认证接口 (AuthController)
- `signup(username, password, email, phone)` - 用户注册
- `login(username, password)` - 用户登录
- `logout(userId)` - 用户登出
- `changePassword(userId, oldPassword, newPassword)` - 修改密码

### 用户接口 (UserController)
- `getUserInfo(userId)` - 获取用户信息
- `updateUserInfo(userId, email, phone)` - 更新用户信息
- `rechargeBalance(userId, amount)` - 充值余额
- `getUserBalance(userId)` - 获取用户余额

### 商品接口 (ItemController)
- `publishItem(name, description, price, sellerId, stock)` - 发布商品
- `getItemById(itemId)` - 获取商品详情
- `getAllItems()` - 获取所有商品
- `searchItems(keyword)` - 搜索商品
- `updateItem(itemId, name, description, price, stock)` - 更新商品

### 订单接口 (OrderController)
- `createOrder(itemId, buyerId, sellerId, totalPrice)` - 创建订单
- `getOrderById(orderId)` - 获取订单详情
- `getOrdersByBuyer(buyerId)` - 获取买家订单
- `getOrdersBySeller(sellerId)` - 获取卖家订单
- `exchangeContactInfo(orderId)` - 交换联系方式

## 数据库设计

系统使用SQLite数据库，包含以下表：
- `users` - 用户表
- `items` - 商品表
- `orders` - 订单表
- `system_logs` - 系统日志表

## 扩展说明

所有方法都标记了 `// TODO - finish me`，需要根据具体业务逻辑实现：
1. 完善实体类的构造函数和业务方法
2. 实现Service层的业务逻辑
3. 实现Controller层的API接口
4. 完善持久化层的数据库操作
5. 实现工具类的具体功能

## 许可证

MIT License
