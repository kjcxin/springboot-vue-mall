-- =====================================================
-- 电商商城系统 数据库初始化脚本
-- 执行方式：mysql -uroot -p < init.sql
-- =====================================================
CREATE DATABASE IF NOT EXISTS `mall` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `mall`;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT,
  `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
  `password`    VARCHAR(100) NOT NULL COMMENT '密码(BCrypt)',
  `nickname`    VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
  `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 商品表
CREATE TABLE IF NOT EXISTS `product` (
  `id`          BIGINT        NOT NULL AUTO_INCREMENT,
  `name`        VARCHAR(100)  NOT NULL COMMENT '商品名称',
  `description` VARCHAR(500)  DEFAULT NULL COMMENT '商品描述',
  `price`       DECIMAL(10,2) NOT NULL COMMENT '价格',
  `stock`       INT           NOT NULL DEFAULT 0 COMMENT '库存',
  `category`    VARCHAR(50)   DEFAULT NULL COMMENT '分类',
  `image`       VARCHAR(255)  DEFAULT NULL COMMENT '图片URL',
  `status`      TINYINT       NOT NULL DEFAULT 1 COMMENT '1上架 0下架',
  `create_time` DATETIME      DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 订单表
CREATE TABLE IF NOT EXISTS `orders` (
  `id`               BIGINT        NOT NULL AUTO_INCREMENT,
  `order_no`         VARCHAR(32)   NOT NULL COMMENT '订单号',
  `user_id`          BIGINT        NOT NULL COMMENT '用户ID',
  `total_amount`     DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
  `status`           TINYINT       NOT NULL DEFAULT 0 COMMENT '0待支付 1已支付 2已取消',
  `receiver_name`    VARCHAR(50)   DEFAULT NULL,
  `receiver_phone`   VARCHAR(20)   DEFAULT NULL,
  `receiver_address` VARCHAR(255)  DEFAULT NULL,
  `create_time`      DATETIME      DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单明细表
CREATE TABLE IF NOT EXISTS `order_item` (
  `id`            BIGINT        NOT NULL AUTO_INCREMENT,
  `order_id`      BIGINT        NOT NULL,
  `product_id`    BIGINT        NOT NULL,
  `product_name`  VARCHAR(100)  NOT NULL,
  `product_image` VARCHAR(255)  DEFAULT NULL,
  `product_price` DECIMAL(10,2) NOT NULL,
  `quantity`      INT           NOT NULL,
  `total_price`   DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_order` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- 种子商品数据
INSERT INTO `product` (`name`, `description`, `price`, `stock`, `category`, `image`) VALUES
('无线蓝牙耳机', '主动降噪，超长续航，支持快充，佩戴舒适', 199.00, 120, '数码', 'https://picsum.photos/seed/mall1/400/400'),
('机械键盘 87键', '青轴手感，RGB 背光，全键无冲，电竞级响应', 349.00, 80, '数码', 'https://picsum.photos/seed/mall2/400/400'),
('4K 显示器 27寸', 'IPS 面板，99% sRGB，Type-C 65W 反向充电', 1899.00, 30, '数码', 'https://picsum.photos/seed/mall3/400/400'),
('纯棉短袖 T恤', '新疆长绒棉，透气舒适，多色可选', 79.00, 500, '服饰', 'https://picsum.photos/seed/mall4/400/400'),
('休闲帆布鞋', '经典百搭，防滑耐磨，轻便透气', 129.00, 300, '服饰', 'https://picsum.photos/seed/mall5/400/400'),
('《深入理解 Java 虚拟机》', 'JVM 进阶必读，涵盖内存、垃圾回收、并发', 99.00, 200, '图书', 'https://picsum.photos/seed/mall6/400/400'),
('《代码整洁之道》', '程序员职业素养与代码质量经典之作', 68.00, 150, '图书', 'https://picsum.photos/seed/mall7/400/400'),
('坚果每日坚果礼盒', '每日一包，营养均衡，30 包混合装', 139.00, 400, '食品', 'https://picsum.photos/seed/mall8/400/400'),
('挂耳咖啡 30 包', '精品阿拉比卡豆，现磨现包，香气浓郁', 89.00, 260, '食品', 'https://picsum.photos/seed/mall9/400/400'),
('北欧简约台灯', '三档调光，护眼无频闪，可折叠设计', 159.00, 90, '家居', 'https://picsum.photos/seed/mall10/400/400'),
('记忆棉靠垫', '人体工学设计，久坐不累，缓解腰背压力', 99.00, 180, '家居', 'https://picsum.photos/seed/mall11/400/400'),
('保温杯 500ml', '316 不锈钢内胆，24 小时保温保冷', 119.00, 220, '家居', 'https://picsum.photos/seed/mall12/400/400');
