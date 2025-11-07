# OpenOtkPlatform 网上购物系统

>这行字是整个仓库中唯一一行由人类敲出的信息

一个基于Spring Boot 3.5.7的现代化网上购物平台，实现了完整的RESTful API和前后端分离架构。项目采用标准的MVC设计模式，提供用户认证、商品管理、订单处理等完整的电商功能。

## 🚀 功能特性

### 核心功能
- ✅ **用户管理** - 注册、登录、密码重置、会话验证
- ✅ **商品系统** - 商品发布、搜索、浏览、库存管理
- ✅ **订单系统** - 订单创建、状态跟踪、联系方式安全交换
- ✅ **余额管理** - 用户余额查询、充值功能
- ✅ **系统日志** - 完整的操作审计和日志记录

### 技术特性
- ✅ **RESTful API设计** - 标准的REST接口规范
- ✅ **前后端分离** - 前端使用纯HTML/CSS/JS，后端提供API
- ✅ **响应式设计** - 支持移动端和桌面端访问
- ✅ **安全防护** - 密码加密、输入验证、SQL注入防护
- ✅ **CORS支持** - 跨域资源共享配置

## 🛠 技术架构

### 后端技术栈
- **Spring Boot 3.5.7** - 现代化Java应用框架
- **Spring Data JPA** - 数据持久化层
- **Spring Web** - RESTful API支持
- **Spring Validation** - 数据验证框架
- **MySQL 8.0** - 关系型数据库
- **HikariCP** - 高性能数据库连接池
- **Spring Boot DevTools** - 开发工具支持

### 前端技术栈
- **HTML5/CSS3** - 页面结构和样式
- **JavaScript (ES6+)** - 前端交互逻辑
- **Fetch API** - HTTP请求处理
- **响应式设计** - 移动端适配

### 设计模式
- **MVC模式** - 整体架构设计
- **Repository模式** - 数据访问层抽象
- **单例模式** - Logger工具类
- **策略模式** - 验证和加密算法
- **观察者模式** - 日志系统

## 📁 项目结构

```
OpenOtkPlatform/
├── src/main/java/com/OpenOtkPlatform/
│   ├── PlatformApplication.java      # SpringBoot主应用类
│   ├── domain/                       # JPA实体类
│   │   ├── User.java                 # 用户实体
│   │   ├── Item.java                 # 商品实体
│   │   ├── Order.java                # 订单实体
│   │   └── SystemLog.java            # 系统日志实体
│   ├── repository/                   # 数据访问层
│   │   ├── UserRepository.java       # 用户数据访问接口
│   │   ├── ItemRepository.java       # 商品数据访问接口
│   │   ├── OrderRepository.java      # 订单数据访问接口
│   │   └── SystemLogRepository.java  # 系统日志数据访问接口
│   ├── service/                      # 业务逻辑层
│   │   ├── UserService.java          # 用户服务
│   │   ├── ItemService.java          # 商品服务
│   │   ├── OrderService.java         # 订单服务
│   │   └── LogService.java           # 日志服务
│   ├── api/                          # REST控制器层
│   │   ├── AuthController.java       # 认证控制器
│   │   ├── UserController.java       # 用户控制器
│   │   ├── ItemController.java       # 商品控制器
│   │   └── OrderController.java      # 订单控制器
│   └── util/                         # 工具类
│       ├── PasswordUtil.java         # 密码加密工具
│       ├── ValidationUtil.java       # 数据验证工具
│       └── Logger.java               # 日志记录器
├── src/main/resources/
│   └── application.properties        # 应用配置文件
├── web/                              # 前端文件
│   ├── index.html                    # 首页
│   ├── user.html                     # 用户管理页面
│   ├── product.html                  # 商品详情页面
│   ├── style.css                     # 样式文件
│   └── script.js                     # JavaScript逻辑
├── database/                         # 数据库相关
│   ├── README.md                     # 数据库文档
│   ├── SETUP_GUIDE.md                # 数据库设置指南
│   ├── init.sql                      # 数据库初始化脚本
│   └── backup.sql                    # 数据库备份脚本
└── pom.xml                          # Maven项目配置
```

## 🚀 快速开始

### 环境要求

- **Java 21+** - 运行环境
- **Maven 3.6+** - 构建工具
- **MySQL 8.0+** - 数据库
- **Git** - 版本控制

### 数据库配置

1. **创建数据库**
```sql
CREATE DATABASE platform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. **配置数据库连接**
编辑 `src/main/resources/application.properties`：
```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/platform?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8&allowPublicKeyRetrieval=true
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA配置
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

3. **初始化数据库（可选）**
```bash
mysql -u root -p platform < database/init.sql
```

### 构建和运行

1. **克隆项目**
```bash
git clone https://github.com/otk-h/OpenOtkPlatform.git
cd OpenOtkPlatform
```

2. **构建项目**
```bash
# 编译项目
mvn clean compile

# # 运行测试
# mvn test

# # 打包为可执行JAR
# mvn clean package
```

3. **运行项目**
```bash
# 开发模式运行
mvn spring-boot:run

# # 或者运行打包后的JAR
# java -jar target/OpenOtkPlatform-1.0.0.jar
```

4. **访问应用**
- 后端API: http://localhost:8080
- 前端页面: 在浏览器中打开 `web/index.html`

## 📚 API接口文档

### 认证接口 (AuthController)

| 方法 | 端点 | 描述 | 请求体 |
|------|------|------|--------|
| POST | `/api/auth/register` | 用户注册 | `{username, password, email, phone}` |
| POST | `/api/auth/login` | 用户登录 | `{username, password}` |
| POST | `/api/auth/logout` | 用户登出 | `userId` |
| GET | `/api/auth/validate` | 会话验证 | `userId` |
| POST | `/api/auth/reset-password` | 密码重置 | `{userId, oldPassword, newPassword}` |

**示例请求：**
```json
// 注册
POST /api/auth/register
{
  "username": "testuser",
  "password": "password123",
  "email": "test@example.com",
  "phone": "13800138000"
}

// 登录
POST /api/auth/login
{
  "username": "testuser",
  "password": "password123"
}
```

### 用户接口 (UserController)

| 方法 | 端点 | 描述 |
|------|------|------|
| GET | `/api/users/{id}` | 获取用户信息 |
| PUT | `/api/users/{id}` | 更新用户信息 |
| POST | `/api/users/{id}/recharge` | 充值余额 |
| GET | `/api/users/{id}/balance` | 获取用户余额 |
| DELETE | `/api/users/{id}` | 删除用户 |

### 商品接口 (ItemController)

| 方法 | 端点 | 描述 |
|------|------|------|
| POST | `/api/items` | 发布商品 |
| GET | `/api/items` | 获取所有商品 |
| GET | `/api/items/{id}` | 获取商品详情 |
| GET | `/api/items/search` | 搜索商品 |
| PUT | `/api/items/{id}` | 更新商品 |
| DELETE | `/api/items/{id}` | 删除商品 |

### 订单接口 (OrderController)

| 方法 | 端点 | 描述 |
|------|------|------|
| POST | `/api/orders` | 创建订单 |
| GET | `/api/orders` | 获取所有订单 |
| GET | `/api/orders/{id}` | 获取订单详情 |
| GET | `/api/orders/buyer/{buyerId}` | 获取买家订单 |
| GET | `/api/orders/seller/{sellerId}` | 获取卖家订单 |
| PUT | `/api/orders/{id}/status` | 更新订单状态 |
| POST | `/api/orders/{id}/cancel` | 取消订单 |
| POST | `/api/orders/{id}/complete` | 完成订单 |
| GET | `/api/orders/{id}/contact` | 交换联系方式 |

## 🗄️ 数据库设计

系统使用MySQL数据库，包含以下核心表：

### users表（用户表）
- `id` - 主键，自增
- `username` - 用户名，唯一
- `password` - 加密密码
- `email` - 邮箱，唯一
- `phone` - 手机号
- `balance` - 账户余额
- `create_time` - 创建时间
- `update_time` - 更新时间

### items表（商品表）
- `id` - 主键，自增
- `name` - 商品名称
- `description` - 商品描述
- `price` - 商品价格
- `seller_id` - 卖家ID
- `stock` - 库存数量
- `status` - 商品状态
- `create_time` - 创建时间
- `update_time` - 更新时间

### orders表（订单表）
- `id` - 主键，自增
- `item_id` - 商品ID
- `buyer_id` - 买家ID
- `seller_id` - 卖家ID
- `total_price` - 订单总价
- `status` - 订单状态
- `create_time` - 创建时间
- `update_time` - 更新时间

### system_logs表（系统日志表）
- `id` - 主键，自增
- `operation_type` - 操作类型
- `user_id` - 用户ID
- `description` - 操作描述
- `create_time` - 创建时间

详细数据库文档请参考：[database/README.md](database/README.md)

## 💻 前端使用

### 页面功能
- **index.html** - 首页，商品浏览和搜索
- **user.html** - 用户管理，登录注册
- **product.html** - 商品详情和购买

### 启动步骤
1. 启动后端服务：
```bash
mvn spring-boot:run
```

2. 在浏览器中打开 `web/index.html`

3. 主要功能：
   - 浏览商品列表
   - 搜索商品
   - 用户注册登录
   - 发布商品
   - 创建订单

## 🔧 开发指南

### 代码规范
- 遵循Java命名规范
- 使用Spring Boot最佳实践
- RESTful API设计规范
- 统一的异常处理机制

### 安全考虑
- **密码加密** - 使用BCrypt加密存储
- **输入验证** - 服务端数据验证
- **SQL注入防护** - 使用JPA参数化查询
- **XSS攻击防护** - 前端输入转义

### 测试
```bash
# 运行单元测试
mvn test

# 运行集成测试
mvn verify
```

### 部署
```bash
# 生产环境打包
mvn clean package -DskipTests

# 运行生产版本
java -jar target/OpenOtkPlatform-1.0.0.jar --spring.profiles.active=prod
```

## 🤝 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 📞 联系方式

- 项目主页: [https://github.com/otk-h/OpenOtkPlatform](https://github.com/otk-h/OpenOtkPlatform)
- 问题反馈: [Issues](https://github.com/otk-h/OpenOtkPlatform/issues)

## 🙏 致谢

感谢所有为这个项目做出贡献的开发者！

## 🚧 未来规划 (TODO)

### 功能增强
- [ ] **UI界面更新** - 现代化UI设计、响应式布局优化、用户体验改进
- [ ] **充值功能完善** - 在线支付集成、充值记录管理、支付安全验证
- [ ] **订单状态更新** - 订单状态流转、状态通知、订单跟踪系统
- [ ] **评论功能** - 商品评价系统、评分机制、评论管理
- [ ] **管理员功能** - 后台管理系统、用户管理、商品审核、数据统计
- [ ] **网页查看数据库内容功能** - 数据库可视化、实时数据监控、数据导出
- [ ] **数据库图片支持** - 向源代码文件夹上传图片，在数据库保存url

---

**OpenOtkPlatform** - 现代化的开源购物平台解决方案
