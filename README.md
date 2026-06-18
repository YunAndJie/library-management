# 在线图书管理系统

基于 Spring Boot 4.0 + MyBatis + Redis + JWT 的在线图书管理系统，提供图书管理、分类管理、借阅管理、用户管理等功能。

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| JDK | 21 | 运行环境 |
| Spring Boot | 4.0.6 | 核心框架 |
| MyBatis | 3.5.19 | ORM 框架 |
| MySQL | 8.x | 关系型数据库 |
| Redis | 7.x | 缓存 / Token 存储 / 限流 |
| JWT (jjwt) | 0.12.7 | 无状态认证 |
| Knife4j | 4.5.0 | API 文档 (Swagger) |
| Lombok | 1.18.46 | 简化代码 |
| Fastjson2 | 2.0.32 | JSON 序列化 |

## 功能模块

### 用户模块
- 用户注册 / 登录 / 退出
- JWT Token 认证（登录存入 Redis，拦截器校验）
- 用户信息修改、状态管理（启用/禁用）
- 角色：普通用户 / 管理员

### 图书模块
- 图书的增删改查、按分类/关键词搜索、分页
- Redis 缓存（单本 10 分钟，列表 10 分钟，写操作自动清除）

### 分类模块
- 多级分类树，支持增删改查
- Redis 缓存（30 分钟），自动构建树形结构

### 借阅模块
- 借书：校验用户状态、图书库存、借阅上限（5 本）、超期记录
- 还书：自动判断是否超期，恢复库存
- 借阅记录查询（按用户/图书/状态）
- @Transactional 事务保证数据一致性

### 其他
- **操作日志 AOP**：记录 Controller 方法调用（路径、耗时、IP），超过 500ms 标记慢请求
- **接口限流**：`@RateLimit` 注解，基于 Redis INCR 的滑动窗口限流
- **超期自动扫描**：定时任务每 60 分钟扫描超期借阅记录
- **全局异常处理**：统一异常响应格式

## 项目结构

```
src/
├── main/java/com/pwenjie/
│   ├── LibraryApplication.java       # 启动类（含 @MapperScan、@EnableScheduling）
│   ├── aop/
│   │   ├── LogAspect.java            # 操作日志切面
│   │   ├── RateLimit.java            # 限流注解
│   │   └── RateLimitAspect.java      # 限流切面
│   ├── common/
│   │   ├── constant/                 # 常量（用户、缓存、系统）
│   │   ├── enums/                    # 枚举（响应码、用户角色、借阅状态、图书状态）
│   │   ├── exception/                # 全局异常处理、业务异常
│   │   ├── interceptor/              # 登录拦截器、跨域配置
│   │   ├── result/                   # 统一响应体 Result<T>
│   │   └── utils/                    # 工具类（JWT、MD5加密、日期、校验）
│   ├── config/                       # 配置类（Redis、Knife4j）
│   ├── controller/                   # 控制器
│   │   ├── UserController.java       # /api/users/*
│   │   ├── BookController.java       # /api/books/*
│   │   ├── CategoryController.java   # /api/categories/*
│   │   ├── BorrowController.java     # /api/borrow/*
│   │   ├── AdminController.java      # /api/admin/*
│   │   └── TestController.java       # /api/test/*
│   ├── dto/
│   │   ├── request/                  # 请求 DTO（含 @Valid 校验）
│   │   └── response/                 # 响应 VO
│   ├── entity/                       # 实体类
│   ├── mapper/                       # MyBatis Mapper（注解式 SQL）
│   └── service/                      # 业务逻辑
│       ├── impl/                     # 实现类
│       └── OverdueScheduler.java     # 超期扫描定时任务
├── main/resources/
│   ├── application.yml               # 主配置（数据库、Redis、JWT）
│   ├── application-dev.yml           # 开发环境配置
│   ├── application-prod.yml          # 生产环境配置（环境变量注入）
│   ├── sql/
│   │   ├── library_init.sql          # 数据库初始化脚本
│   │   └── rebuild.sql               # 删表重建脚本
│   └── static/                       # 前端页面
│       ├── index.html                # 主页面（SPA）
│       ├── css/style.css             # 样式
│       └── js/
│           ├── api.js                 # API 请求封装
│           └── app.js                 # 页面逻辑
```

## 快速开始

### 1. 环境要求
- JDK 21+
- MySQL 8.0+
- Redis 7.0+
- Maven 3.6+

### 2. 创建数据库
在 MySQL 中执行 `src/main/resources/sql/rebuild.sql`，或手动执行：
```sql
CREATE DATABASE IF NOT EXISTS libaray_db DEFAULT CHARACTER SET utf8mb4;
```
然后运行 SQL 脚本中的建表语句。

### 3. 修改配置
根据环境修改 `application.yml` 中的数据库和 Redis 连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/libaray_db
    username: root
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
```

### 4. 启动项目
```bash
mvn clean compile
mvn spring-boot:run
```
或直接在 IDE 中运行 `LibraryApplication.java`。

### 5. 访问
- 前端页面：`http://localhost:8080/api/index.html`
- API 文档：`http://localhost:8080/api/doc.html`
- 测试接口：`http://localhost:8080/api/test/hello`

## 默认账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | admin | 123456 | 预设管理员，可管理所有模块 |
| 普通用户 | 自行注册 | — | 可浏览图书、借阅 |

## API 接口概览

### 用户 `/api/users`
| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/register` | 注册 | 否 |
| POST | `/login` | 登录 | 否 |
| POST | `/logout` | 退出 | 是 |
| GET | `/{id}` | 查用户 | 是 |
| GET | `/page` | 分页列表 | 是 |
| PUT | `/{id}` | 更新 | 是 |
| PUT | `/{id}/status/{status}` | 启/禁用 | 是 |
| DELETE | `/{id}` | 删除 | 是 |

### 图书 `/api/books`
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/` | 添加图书 |
| GET | `/{id}` | 查图书（含缓存） |
| GET | `/page` | 分页列表（含缓存） |
| GET | `/search?keyword=` | 搜索 |
| GET | `/category/{id}` | 按分类查 |
| PUT | `/{id}` | 更新 |
| DELETE | `/{id}` | 删除 |

### 分类 `/api/categories`
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/` | 添加分类 |
| GET | `/` | 全部（树形结构） |
| GET | `/{id}` | 查单个 |
| GET | `/parent/{id}` | 子分类（扁平） |
| PUT | `/{id}` | 更新 |
| DELETE | `/{id}` | 删除（有子分类则拒绝） |

### 借阅 `/api/borrow`
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/` | 借书（限流 3次/60s） |
| PUT | `/{id}/return` | 还书 |
| GET | `/{id}` | 查记录 |
| GET | `/user/{id}` | 按用户查 |
| GET | `/book/{id}` | 按图书查 |
| GET | `/borrowing` | 借阅中 |
| GET | `/overdue` | 超期记录 |

### 管理 `/api/admin`
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/dashboard` | 仪表盘（用户/图书/分类/借阅总数） |

## Redis 缓存策略

| 缓存 | Key 格式 | 过期时间 |
|------|----------|----------|
| 用户 Token | `USER_TOKEN:{token}` | 30 分钟 |
| 单本图书 | `BOOK:{id}` | 10 分钟 |
| 图书列表 | `BOOK_LIST:{type}:{param}` | 10 分钟 |
| 单条分类 | `CATEGORY:{id}` | 30 分钟 |
| 分类列表 | `CATEGORY_LIST:{type}` | 30 分钟 |
| 借阅列表 | `BORROW_LIST:*` | 5 分钟 |
| 限流计数 | `RATE_LIMIT:{class}:{method}:{ip}` | 窗口时长 |

> 写操作（增/删/改）会自动清除相关列表缓存，保证数据一致性。

## 借阅规则
- 每人最多同时借阅 **5 本**图书
- 默认借阅期限 **30 天**
- 有超期未还记录时，**不可再借**
- 还书时自动判断是否超期，超期则标记为"已超期"
- 定时任务每小时自动扫描超期记录

## 密码加密
采用 **MD5 + 盐值** 加密：
```
密文 = MD5("library_salt_2024" + 原始密码)
```

## 响应格式

统一响应体 `Result<T>`：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... },
  "timestamp": 1718480000000
}
```

| code | 说明 |
|------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 系统内部错误 |
| 1001-1004 | 用户模块业务错误 |
| 2001-2003 | 图书模块业务错误 |
| 3001-3003 | 借阅模块业务错误 |

