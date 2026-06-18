-- ============================================
-- 在线图书管理系统 - 数据库初始化脚本
-- 数据库名称: libaray_db
-- ============================================

CREATE DATABASE IF NOT EXISTS libaray_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE libaray_db;

-- ============================================
-- 用户表
-- ============================================
CREATE TABLE IF NOT EXISTS `user` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '用户ID',
    `username`      VARCHAR(50)     NOT NULL                 COMMENT '用户名',
    `password`      VARCHAR(100)    NOT NULL                 COMMENT '密码（MD5加密）',
    `email`         VARCHAR(100)    DEFAULT NULL             COMMENT '邮箱',
    `phone`         VARCHAR(20)     DEFAULT NULL             COMMENT '手机号',
    `avatar`        VARCHAR(500)    DEFAULT ''               COMMENT '头像URL',
    `role`          INT             NOT NULL DEFAULT 0       COMMENT '角色 0-普通用户 1-管理员',
    `status`        INT             NOT NULL DEFAULT 1       COMMENT '状态 0-禁用 1-启用',
    `last_login_time` DATETIME     DEFAULT NULL             COMMENT '最后登录时间',
    `create_time`   DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 插入默认管理员
INSERT INTO `user` (`username`, `password`, `email`, `role`, `status`) VALUES
('admin', '6b8c32f80bc2269e481494e4547290cd', 'admin@library.com', 1, 1);

-- ============================================
-- 图书分类表
-- ============================================
CREATE TABLE IF NOT EXISTS `categroy` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '分类ID',
    `name`          VARCHAR(50)     NOT NULL                 COMMENT '分类名称',
    `description`   VARCHAR(200)    DEFAULT NULL             COMMENT '分类描述',
    `parent_id`     BIGINT          DEFAULT NULL             COMMENT '父分类ID',
    `sort`          INT             NOT NULL DEFAULT 0       COMMENT '排序（越小越靠前）',
    `create_time`   DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书分类表';

-- 插入默认分类
INSERT INTO `categroy` (`name`, `description`, `sort`) VALUES
('计算机科学', '计算机相关书籍', 1),
('文学小说', '文学、小说类书籍', 2),
('历史传记', '历史、传记类书籍', 3),
('科学技术', '自然科学、工程技术类', 4),
('哲学心理', '哲学、心理学类', 5);

-- ============================================
-- 图书表
-- ============================================
CREATE TABLE IF NOT EXISTS `book` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '图书ID',
    `isbn`          VARCHAR(20)     NOT NULL                 COMMENT 'ISBN号',
    `title`         VARCHAR(200)    NOT NULL                 COMMENT '书名',
    `author`        VARCHAR(100)    NOT NULL                 COMMENT '作者',
    `publisher`     VARCHAR(100)    DEFAULT NULL             COMMENT '出版社',
    `publish_time`  DATE            DEFAULT NULL             COMMENT '出版日期',
    `categroy_id`   BIGINT          DEFAULT NULL             COMMENT '分类ID',
    `price`         DECIMAL(10,2)   DEFAULT 0.00             COMMENT '价格',
    `stock`         INT             NOT NULL DEFAULT 0       COMMENT '库存数量',
    `cover_url`     VARCHAR(500)    DEFAULT NULL             COMMENT '封面图片URL',
    `description`   VARCHAR(500)    DEFAULT NULL             COMMENT '图书描述',
    `status`        INT             NOT NULL DEFAULT 1       COMMENT '状态 0-下架 1-在架 3-已借阅',
    `create_time`   DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_isbn` (`isbn`),
    KEY `idx_title` (`title`),
    KEY `idx_author` (`author`),
    KEY `idx_categroy_id` (`categroy_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书表';

-- 插入示例图书
INSERT INTO `book` (`isbn`, `title`, `author`, `publisher`, `price`, `stock`, `description`) VALUES
('978-7-111-11111-1', 'Java编程思想', 'Bruce Eckel', '机械工业出版社', 108.00, 10, 'Java经典入门书籍'),
('978-7-302-22222-2', 'Spring实战', 'Craig Walls', '人民邮电出版社', 89.00, 15, 'Spring框架权威指南'),
('978-7-115-33333-3', '深入理解计算机系统', 'Randal E. Bryant', '机械工业出版社', 139.00, 8, '计算机科学经典教材'),
('978-7-121-44444-4', '活着', '余华', '作家出版社', 29.00, 20, '中国当代文学经典'),
('978-7-542-55555-5', '三体', '刘慈欣', '重庆出版社', 62.00, 25, '雨果奖获奖科幻小说');

-- ============================================
-- 借阅记录表
-- ============================================
CREATE TABLE IF NOT EXISTS `borrow_record` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '记录ID',
    `user_id`       BIGINT          NOT NULL                 COMMENT '用户ID',
    `book_id`       BIGINT          NOT NULL                 COMMENT '图书ID',
    `borrow_time`   DATETIME        NOT NULL                 COMMENT '借阅时间',
    `due_time`      DATETIME        NOT NULL                 COMMENT '应还时间',
    `return_time`   DATETIME        DEFAULT NULL             COMMENT '归还时间',
    `status`        INT             NOT NULL DEFAULT 0       COMMENT '状态 0-借阅中 1-已归还 2-已超期',
    `remark`        VARCHAR(200)    DEFAULT NULL             COMMENT '备注',
    `create_time`   DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_book_id` (`book_id`),
    KEY `idx_status` (`status`),
    KEY `idx_due_time` (`due_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借阅记录表';
