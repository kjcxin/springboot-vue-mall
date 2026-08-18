-- =====================================================
-- 电商商城系统 数据库初始化脚本（完整版）
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
  `avatar`      VARCHAR(255) DEFAULT '🙂' COMMENT '头像(emoji或URL)',
  `role`        TINYINT      NOT NULL DEFAULT 0 COMMENT '0普通用户 1管理员',
  `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '1正常 0禁用',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 商品分类表（多级，parent_id 自关联）
CREATE TABLE IF NOT EXISTS `category` (
  `id`          BIGINT      NOT NULL AUTO_INCREMENT,
  `parent_id`   BIGINT      NOT NULL DEFAULT 0 COMMENT '父分类ID，0为顶级',
  `name`        VARCHAR(50) NOT NULL,
  `sort`        INT         DEFAULT 0 COMMENT '排序，越小越靠前',
  `status`      TINYINT     DEFAULT 1 COMMENT '1启用 0停用',
  `create_time` DATETIME    DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 商品表
CREATE TABLE IF NOT EXISTS `product` (
  `id`          BIGINT        NOT NULL AUTO_INCREMENT,
  `name`        VARCHAR(100)  NOT NULL COMMENT '商品名称',
  `description` VARCHAR(500)  DEFAULT NULL,
  `price`       DECIMAL(10,2) NOT NULL COMMENT '价格',
  `stock`       INT           NOT NULL DEFAULT 0 COMMENT '库存',
  `sales`       INT           NOT NULL DEFAULT 0 COMMENT '销量',
  `category_id` BIGINT        DEFAULT NULL COMMENT '分类ID',
  `image`       VARCHAR(255)  DEFAULT NULL COMMENT '图片(emoji或URL)',
  `status`      TINYINT       NOT NULL DEFAULT 1 COMMENT '1上架 0下架',
  `create_time` DATETIME      DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category_id`),
  KEY `idx_sales` (`sales`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 收货地址表
CREATE TABLE IF NOT EXISTS `address` (
  `id`               BIGINT       NOT NULL AUTO_INCREMENT,
  `user_id`          BIGINT       NOT NULL,
  `receiver_name`    VARCHAR(50)  NOT NULL,
  `receiver_phone`   VARCHAR(20)  NOT NULL,
  `receiver_address` VARCHAR(255) NOT NULL,
  `is_default`       TINYINT      NOT NULL DEFAULT 0 COMMENT '1默认地址',
  `create_time`      DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `update_time`      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

-- 收藏表
CREATE TABLE IF NOT EXISTS `favorite` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT,
  `user_id`     BIGINT   NOT NULL,
  `product_id`  BIGINT   NOT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product` (`user_id`, `product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- 订单表（完整状态机）
CREATE TABLE IF NOT EXISTS `orders` (
  `id`               BIGINT        NOT NULL AUTO_INCREMENT,
  `order_no`         VARCHAR(32)   NOT NULL COMMENT '订单号',
  `user_id`          BIGINT        NOT NULL,
  `total_amount`     DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
  `status`           TINYINT       NOT NULL DEFAULT 0 COMMENT '0待支付 1待发货 2待收货 3已完成 4已取消',
  `receiver_name`    VARCHAR(50)   DEFAULT NULL,
  `receiver_phone`   VARCHAR(20)   DEFAULT NULL,
  `receiver_address` VARCHAR(255)  DEFAULT NULL,
  `pay_time`         DATETIME      DEFAULT NULL COMMENT '支付时间',
  `ship_time`        DATETIME      DEFAULT NULL COMMENT '发货时间',
  `complete_time`    DATETIME      DEFAULT NULL COMMENT '完成时间',
  `create_time`      DATETIME      DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user` (`user_id`),
  KEY `idx_status` (`status`)
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
  KEY `idx_order` (`order_id`),
  KEY `idx_product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- 评价表
CREATE TABLE IF NOT EXISTS `review` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT,
  `order_id`    BIGINT       DEFAULT NULL,
  `product_id`  BIGINT       NOT NULL,
  `user_id`     BIGINT       NOT NULL,
  `rating`      TINYINT      NOT NULL COMMENT '评分 1-5',
  `content`     VARCHAR(500) DEFAULT NULL COMMENT '评价内容',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评价表';

-- =====================================================
-- 种子数据
-- =====================================================

-- 分类（多级）
INSERT INTO `category` (`id`, `parent_id`, `name`, `sort`) VALUES
(1,  0, '数码', 1),
(2,  0, '服饰', 2),
(3,  0, '图书', 3),
(4,  0, '食品', 4),
(5,  0, '家居', 5),
(6,  1, '手机', 1),
(7,  1, '电脑配件', 2),
(8,  1, '影音娱乐', 3),
(9,  2, '男装', 1),
(10, 2, '女装', 2),
(11, 4, '零食', 1),
(12, 4, '饮品', 2);

-- 商品（14 条，对应分类 + emoji 图标）
INSERT INTO `product` (`name`, `description`, `price`, `stock`, `sales`, `category_id`, `image`) VALUES
('无线蓝牙耳机', '主动降噪，超长续航，支持快充，佩戴舒适', 199.00, 120, 320, 8, '🎧'),
('机械键盘 87键', '青轴手感，RGB 背光，全键无冲，电竞级响应', 349.00, 80, 210, 7, '⌨️'),
('4K 显示器 27寸', 'IPS 面板，99% sRGB，Type-C 65W 反向充电', 1899.00, 30, 88, 7, '🖥️'),
('旗舰智能手机', '6.7 英寸全面屏，120Hz 高刷，旗舰影像', 4999.00, 50, 156, 6, '📱'),
('纯棉短袖 T恤', '新疆长绒棉，透气舒适，多色可选', 79.00, 500, 980, 9, '👕'),
('休闲帆布鞋', '经典百搭，防滑耐磨，轻便透气', 129.00, 300, 640, 9, '👟'),
('连衣裙 夏季新款', '轻薄透气，收腰显瘦，多色可选', 199.00, 200, 410, 10, '👗'),
('《深入理解 Java 虚拟机》', 'JVM 进阶必读，涵盖内存、垃圾回收、并发', 99.00, 200, 1300, 3, '📖'),
('《代码整洁之道》', '程序员职业素养与代码质量经典之作', 68.00, 150, 760, 3, '📕'),
('坚果每日坚果礼盒', '每日一包，营养均衡，30 包混合装', 139.00, 400, 520, 11, '🥜'),
('挂耳咖啡 30 包', '精品阿拉比卡豆，现磨现包，香气浓郁', 89.00, 260, 430, 12, '☕'),
('北欧简约台灯', '三档调光，护眼无频闪，可折叠设计', 159.00, 90, 280, 5, '💡'),
('记忆棉靠垫', '人体工学设计，久坐不累，缓解腰背压力', 99.00, 180, 350, 5, '🛋️'),
('保温杯 500ml', '316 不锈钢内胆，24 小时保温保冷', 119.00, 220, 610, 5, '🥤');
