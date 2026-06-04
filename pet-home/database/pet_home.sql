-- 宠物之家数据库脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS pet_home DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE pet_home;

-- ----------------------------
-- 1. 用户表
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `phone` VARCHAR(20) UNIQUE COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `avatar` VARCHAR(255) DEFAULT '/assets/images/default-avatar.png' COMMENT '头像',
    `address` VARCHAR(255) COMMENT '收货地址',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_username` (`username`),
    INDEX `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 2. 管理员表
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '管理员用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `phone` VARCHAR(20) COMMENT '手机号',
    `real_name` VARCHAR(50) COMMENT '真实姓名',
    `role` VARCHAR(20) DEFAULT 'admin' COMMENT '角色',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- ----------------------------
-- 3. 商品表
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `name` VARCHAR(100) NOT NULL COMMENT '商品名称',
    `category` VARCHAR(50) NOT NULL COMMENT '商品分类',
    `pet_type` VARCHAR(50) NOT NULL COMMENT '适用宠物类型(猫/狗/通用)',
    `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
    `stock` INT DEFAULT 0 COMMENT '库存',
    `image` VARCHAR(255) COMMENT '商品图片',
    `description` TEXT COMMENT '商品描述',
    `spec` VARCHAR(100) COMMENT '规格',
    `status` TINYINT DEFAULT 1 COMMENT '状态(1上架 0下架)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_category` (`category`),
    INDEX `idx_pet_type` (`pet_type`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ----------------------------
-- 4. 购物车表
-- ----------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
    `user_id` INT NOT NULL COMMENT '用户ID',
    `product_id` INT NOT NULL COMMENT '商品ID',
    `quantity` INT DEFAULT 1 COMMENT '数量',
    `spec` VARCHAR(100) COMMENT '规格',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- ----------------------------
-- 5. 商品订单表
-- ----------------------------
DROP TABLE IF EXISTS `product_order`;
CREATE TABLE `product_order` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    `user_id` INT NOT NULL COMMENT '用户ID',
    `total_price` DECIMAL(10,2) NOT NULL COMMENT '总价',
    `status` TINYINT DEFAULT 1 COMMENT '状态(1未支付 2待发货 3已发货 4已签收)',
    `receiver_name` VARCHAR(50) COMMENT '收货人',
    `receiver_phone` VARCHAR(20) COMMENT '联系电话',
    `receiver_address` VARCHAR(255) COMMENT '收货地址',
    `remark` VARCHAR(255) COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `pay_time` DATETIME COMMENT '支付时间',
    `ship_time` DATETIME COMMENT '发货时间',
    `receive_time` DATETIME COMMENT '收货时间',
    PRIMARY KEY (`id`),
    INDEX `idx_order_no` (`order_no`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status` (`status`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品订单表';

-- ----------------------------
-- 6. 订单商品明细表
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    `order_id` INT NOT NULL COMMENT '订单ID',
    `product_id` INT NOT NULL COMMENT '商品ID',
    `product_name` VARCHAR(100) COMMENT '商品名称(冗余)',
    `price` DECIMAL(10,2) NOT NULL COMMENT '单价',
    `quantity` INT NOT NULL COMMENT '数量',
    `spec` VARCHAR(100) COMMENT '规格',
    PRIMARY KEY (`id`),
    INDEX `idx_order_id` (`order_id`),
    FOREIGN KEY (`order_id`) REFERENCES `product_order`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`product_id`) REFERENCES `product`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品明细表';

-- ----------------------------
-- 7. 宠物档案表
-- ----------------------------
DROP TABLE IF EXISTS `pet`;
CREATE TABLE `pet` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '宠物ID',
    `user_id` INT NOT NULL COMMENT '用户ID',
    `name` VARCHAR(50) NOT NULL COMMENT '宠物名',
    `species` VARCHAR(50) NOT NULL COMMENT '物种(猫/狗/其他)',
    `breed` VARCHAR(50) COMMENT '品种',
    `age` INT COMMENT '年龄',
    `gender` VARCHAR(10) COMMENT '性别',
    `weight` DECIMAL(5,2) COMMENT '体重(kg)',
    `image` VARCHAR(255) COMMENT '照片',
    `health_status` VARCHAR(100) COMMENT '健康状况',
    `remark` VARCHAR(255) COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物档案表';

-- ----------------------------
-- 8. 寄养套餐表
-- ----------------------------
DROP TABLE IF EXISTS `foster_package`;
CREATE TABLE `foster_package` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '套餐ID',
    `name` VARCHAR(100) NOT NULL COMMENT '套餐名称',
    `description` TEXT COMMENT '套餐描述',
    `price_per_day` DECIMAL(10,2) NOT NULL COMMENT '每日价格',
    `services` VARCHAR(255) COMMENT '包含服务(JSON格式)',
    `status` TINYINT DEFAULT 1 COMMENT '状态(1启用 0停用)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='寄养套餐表';

-- ----------------------------
-- 9. 寄养预约订单表
-- ----------------------------
DROP TABLE IF EXISTS `foster_order`;
CREATE TABLE `foster_order` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '预约ID',
    `order_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '预约单号',
    `user_id` INT NOT NULL COMMENT '用户ID',
    `pet_id` INT NOT NULL COMMENT '宠物ID',
    `package_id` INT NOT NULL COMMENT '套餐ID',
    `start_date` DATE NOT NULL COMMENT '寄养开始日期',
    `end_date` DATE NOT NULL COMMENT '寄养结束日期',
    `total_days` INT NOT NULL COMMENT '寄养天数',
    `total_price` DECIMAL(10,2) NOT NULL COMMENT '总费用',
    `status` TINYINT DEFAULT 1 COMMENT '状态(1待审核 2待缴费 3寄养中 4已完结 5已取消)',
    `remark` VARCHAR(255) COMMENT '备注',
    `reject_reason` VARCHAR(255) COMMENT '驳回原因',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `audit_time` DATETIME COMMENT '审核时间',
    `complete_time` DATETIME COMMENT '完成时间',
    PRIMARY KEY (`id`),
    INDEX `idx_order_no` (`order_no`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_pet_id` (`pet_id`),
    INDEX `idx_status` (`status`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`pet_id`) REFERENCES `pet`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`package_id`) REFERENCES `foster_package`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='寄养预约订单表';

-- ----------------------------
-- 初始化管理员数据
-- ----------------------------
INSERT INTO `admin` (`username`, `password`, `phone`, `real_name`) VALUES
('admin', 'admin123', '13800138000', '系统管理员');

-- ----------------------------
-- 初始化寄养套餐数据
-- ----------------------------
INSERT INTO `foster_package` (`name`, `description`, `price_per_day`, `services`) VALUES
('基础套餐', '提供基本住宿、饮食照料', 50.00, '["基本住宿", "每日三餐", "基础清洁"]'),
('标准套餐', '基础服务+日常健康监测', 80.00, '["标准住宿", "每日三餐", "日常清洁", "健康监测"]'),
('豪华套餐', '标准服务+专属护理+玩耍时间', 120.00, '["豪华住宿", "营养三餐", "专业清洁", "健康监测", "专属护理", "每日玩耍"]');

-- ----------------------------
-- 初始化商品数据
-- ----------------------------
INSERT INTO `product` (`name`, `category`, `pet_type`, `price`, `stock`, `image`, `description`, `spec`, `status`) VALUES
('天然狗粮成犬粮', '狗粮', '狗', 128.00, 100, '/assets/images/product/dog-food-1.jpg', '精选天然食材，营养均衡，适合1-7岁成犬', '2.5kg/袋', 1),
('天然猫粮成猫粮', '猫粮', '猫', 98.00, 150, '/assets/images/product/cat-food-1.jpg', '高蛋白配方，关爱猫咪泌尿系统健康', '2kg/袋', 1),
('宠物自动饮水机', '餐具水具', '通用', 89.00, 80, '/assets/images/product/water-fountain.jpg', '循环过滤活水，吸引宠物多喝水', '2.5L', 1),
('猫爬架大型', '家居笼具', '猫', 268.00, 40, '/assets/images/product/cat-tree.jpg', '多层设计，天然剑麻柱，稳固安全', '高1.5m', 1),
('宠物外出背包', '外出用品', '通用', 75.00, 60, '/assets/images/product/pet-bag.jpg', '透气舒适，可爱造型，适用5kg以下宠物', '标准版', 1),
('狗狗牵引绳', '外出用品', '狗', 45.00, 120, '/assets/images/product/dog-leash.jpg', '防爆冲设计，可调节长度', 'L码', 1),
('宠物电热毯', '窝垫床品', '通用', 65.00, 50, '/assets/images/product/pet-blanket.jpg', '恒温保暖，三档可调，USB供电', '中号', 1),
('猫咪逗猫棒', '玩具', '猫', 25.00, 200, '/assets/images/product/cat-wand.jpg', '羽毛设计，互动增情', '标准版', 1),
('狗狗飞盘', '玩具', '狗', 35.00, 150, '/assets/images/product/dog-frisbee.jpg', '柔软不伤牙龈，适合户外玩耍', '标准版', 1),
('宠物洗浴香波', '清洁护理', '通用', 42.00, 90, '/assets/images/product/pet-shampoo.jpg', '温和配方，除螨抑菌，留香持久', '500ml', 1);
