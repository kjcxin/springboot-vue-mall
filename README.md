# Mall 商城 · 前后端分离电商系统

一个功能完整的前后端分离电商商城，用于学习与实践全栈开发。涵盖用户认证、商品浏览、购物车、订单状态机、收货地址、收藏、评价，以及完整的后台管理系统。

## 技术栈

| 端 | 技术 |
| --- | --- |
| 后端 | Spring Boot 3 · MyBatis-Plus · MySQL 8 · Redis · JWT · BCrypt |
| 前端 | Vue 3 · Vite · Vue Router · Pinia · Axios · Element Plus |

## 功能特性

### 用户侧（C 端）
- **认证**：注册 / 登录（图形验证码 + 记住登录）、JWT 无状态鉴权、BCrypt 密码加密、修改密码
- **商品**：多级分类、分页列表、关键词搜索、价格区间筛选、销量/价格排序、销量统计
- **购物车**：基于 Redis Hash 存储、勾选结算、批量删除、数量修改
- **订单**：完整状态机 `待支付 → 待发货 → 待收货 → 已完成 / 已取消`、模拟支付、取消恢复库存、确认收货
- **收货地址**：地址簿增删改查、默认地址
- **收藏**：收藏 / 取消 / 列表
- **评价**：收货后评价（星级 + 文字）、商品评价列表
- **个人中心**：资料修改、头像、密码

### 管理端（B 端）
- **数据看板**：商品/用户/订单/分类数、销售额、今日订单、待发货、最近订单
- **商品管理**：增删改查、上下架、库存
- **分类管理**：多级分类增删改查
- **订单管理**：查询、发货、关闭（恢复库存）
- **用户管理**：查询、禁用/启用

## 默认账号

| 账号 | 密码 | 角色 |
| --- | --- | --- |
| admin | admin123 | 管理员（首次启动自动创建） |

> 普通用户请在注册页自行注册。

## 项目结构

```
mall
├── backend/                          # Spring Boot 后端
│   ├── src/main/java/com/mall
│   │   ├── common/                   # 统一响应、异常、分页
│   │   ├── config/                   # Web/MyBatis-Plus/Jackson 配置、管理员初始化
│   │   ├── security/                 # JWT、鉴权拦截器、管理员拦截器、验证码
│   │   ├── entity/                   # 实体（user/product/category/order/address/favorite/review）
│   │   ├── mapper/                   # MyBatis-Plus Mapper
│   │   ├── service/                  # 业务逻辑（含订单状态机、看板统计）
│   │   ├── controller/               # C 端 + admin 端 REST 接口
│   │   └── dto/                      # 请求/响应对象
│   └── pom.xml
├── frontend/                         # Vue 3 前端
│   └── src
│       ├── api/                      # Axios 封装与接口定义
│       ├── stores/                   # Pinia 状态（用户）
│       ├── router/                   # 路由 + 登录/管理员守卫
│       ├── components/               # 导航栏
│       └── views/                    # C 端页面 + admin 页面
├── sql/init.sql                      # 数据库初始化脚本（含种子数据）
└── docker-compose.yml                # MySQL + Redis 一键启动
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

首次启动自动执行 `sql/init.sql`，创建数据库、表结构并写入种子数据（14 商品、12 分类）。

### 2. 启动后端

```bash
cd backend
./mvnw spring-boot:run     # Windows 使用 mvnw.cmd
```

后端运行在 `http://localhost:8080`，首次启动自动创建管理员 `admin/admin123`。

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端运行在 `http://localhost:5173`，`/api` 自动代理到后端。管理后台入口：`http://localhost:5173/admin`（需管理员登录）。

## API 一览

### 公开接口
| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/captcha` | 获取图形验证码 |
| POST | `/api/auth/register` | 注册 |
| POST | `/api/auth/login` | 登录（验证码 + 记住登录） |
| GET | `/api/categories` | 分类树 |
| GET | `/api/products` | 商品分页/搜索/筛选/排序 |
| GET | `/api/products/{id}` | 商品详情 |
| GET | `/api/products/{id}/reviews` | 商品评价列表 |

### 登录后接口
| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET/POST/PUT/DELETE | `/api/cart` | 购物车 |
| GET/POST | `/api/orders` | 订单列表 / 下单 |
| PUT | `/api/orders/{id}/pay` | 模拟支付 |
| PUT | `/api/orders/{id}/cancel` | 取消订单 |
| PUT | `/api/orders/{id}/confirm` | 确认收货 |
| GET/POST/PUT/DELETE | `/api/addresses` | 收货地址 |
| GET/POST/DELETE | `/api/favorites` | 收藏 |
| POST | `/api/reviews` | 评价 |
| GET/PUT | `/api/user/profile` `/password` | 个人中心 |

### 管理端接口（需管理员角色）
| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/admin/dashboard` | 数据看板 |
| CRUD | `/api/admin/products` | 商品管理 |
| CRUD | `/api/admin/categories` | 分类管理 |
| GET/PUT | `/api/admin/orders` | 订单管理（发货/关闭） |
| GET/PUT | `/api/admin/users` | 用户管理（禁用/启用） |

鉴权方式：请求头携带 `Authorization: Bearer <token>`。

## 数据库设计

- `user` 用户表（角色、状态、头像）
- `category` 分类表（多级自关联）
- `product` 商品表（分类、销量、上下架）
- `address` 收货地址表
- `favorite` 收藏表
- `orders` / `order_item` 订单表（状态机、时间字段）
- `review` 评价表

购物车数据存储在 Redis（key：`mall:cart:{userId}`，Hash 结构）；验证码存储在 Redis（5 分钟有效、一次性）。

## 说明

- 支付为**模拟支付**（点击即支付成功），未接入真实支付网关，订单状态机可在此基础上扩展。
- 商品图片使用 emoji 占位图标（与商品一一对应、离线可用），如需真实图片可将 `image` 字段替换为 URL 并改回 `<el-image>` 渲染。

## 许可证

[MIT](./LICENSE)
