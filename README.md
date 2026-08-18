# Mall 商城 · 前后端分离电商系统

一个用于学习与实践的前后端分离电商商城项目，涵盖用户认证、商品浏览、购物车、下单等核心电商流程。

## 技术栈

| 端 | 技术 |
| --- | --- |
| 后端 | Spring Boot 3 · MyBatis-Plus · MySQL 8 · Redis · JWT · BCrypt |
| 前端 | Vue 3 · Vite · Vue Router · Pinia · Axios · Element Plus |

## 功能特性

- **用户认证**：注册 / 登录，JWT 无状态鉴权，BCrypt 密码加密
- **商品浏览**：分页列表、关键词搜索、分类筛选、商品详情
- **购物车**：基于 Redis Hash 存储，增删改查、数量累加与库存校验
- **下单**：从购物车生成订单，事务内校验并扣减库存（防超卖）、生成订单与明细、清空购物车
- **我的订单**：订单列表与明细查询
- **统一响应**：统一返回结构、全局异常处理、参数校验、跨域配置

## 项目结构

```
mall
├── backend/                  # Spring Boot 后端
│   ├── src/main/java/com/mall
│   │   ├── common/           # 统一响应、异常、分页
│   │   ├── config/           # Web/MyBatis-Plus/Jackson 配置
│   │   ├── security/         # JWT 工具、鉴权拦截器、用户上下文
│   │   ├── entity/           # 实体
│   │   ├── mapper/           # MyBatis-Plus Mapper
│   │   ├── service/          # 业务逻辑
│   │   ├── controller/       # REST 接口
│   │   └── dto/              # 请求/响应对象
│   └── pom.xml
├── frontend/                 # Vue 3 前端
│   └── src
│       ├── api/              # Axios 封装与接口定义
│       ├── stores/           # Pinia 状态（用户）
│       ├── router/           # 路由与登录守卫
│       ├── components/       # 导航栏
│       └── views/            # 页面
├── sql/init.sql              # 数据库初始化脚本（含种子商品）
└── docker-compose.yml        # MySQL + Redis 一键启动
```

## 快速开始

### 环境要求

- JDK 17+
- Node.js 18+
- Docker（用于启动 MySQL 与 Redis）

### 1. 启动 MySQL 与 Redis

```bash
docker compose up -d
```

首次启动会自动执行 `sql/init.sql`，创建数据库、表结构并写入 12 条种子商品数据。

> 若不想用 Docker，也可本地安装 MySQL 8 与 Redis，手动执行 `sql/init.sql`。

### 2. 启动后端

```bash
cd backend
# Windows 使用 mvnw.cmd，Linux/Mac 使用 ./mvnw
./mvnw spring-boot:run
```

后端默认运行在 `http://localhost:8080`。数据库/Redis 连接信息见 `backend/src/main/resources/application.yml`（默认 `root/root`，可按需修改）。

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端默认运行在 `http://localhost:5173`，开发环境下 `/api` 请求会自动代理到后端 8080 端口。

## API 一览

| 方法 | 路径 | 说明 | 鉴权 |
| --- | --- | --- | --- |
| POST | `/api/auth/register` | 注册 | 否 |
| POST | `/api/auth/login` | 登录，返回 token | 否 |
| GET | `/api/products` | 商品分页/搜索/筛选 | 否 |
| GET | `/api/products/{id}` | 商品详情 | 否 |
| GET | `/api/cart` | 购物车列表 | 是 |
| POST | `/api/cart` | 加入购物车 | 是 |
| PUT | `/api/cart/{productId}` | 修改数量 | 是 |
| DELETE | `/api/cart/{productId}` | 删除购物车项 | 是 |
| POST | `/api/orders` | 创建订单 | 是 |
| GET | `/api/orders` | 我的订单 | 是 |
| GET | `/api/orders/{id}` | 订单详情 | 是 |

鉴权方式：请求头携带 `Authorization: Bearer <token>`。

## 数据库设计

- `user` 用户表
- `product` 商品表
- `orders` 订单表（`order` 为 MySQL 关键字，故用复数）
- `order_item` 订单明细表

购物车数据存储在 Redis（key 格式：`mall:cart:{userId}`，Hash 结构）。

## 说明

- 该项目为学习实践项目，支付环节为「待支付」状态占位，未接入真实支付网关。
- 商品图片使用 emoji 占位图标（与商品一一对应、离线可用），如需真实图片可将 `image` 字段替换为 URL 并改回 `<el-image>` 渲染。

## 许可证

[MIT](./LICENSE)
