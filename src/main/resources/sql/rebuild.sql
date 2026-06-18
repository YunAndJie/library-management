USE libaray_db;

-- 先删旧表
DROP TABLE IF EXISTS borrow_record;
DROP TABLE IF EXISTS book;
DROP TABLE IF EXISTS categroy;
DROP TABLE IF EXISTS user;

-- 用户表
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    avatar VARCHAR(500) DEFAULT '',
    role INT NOT NULL DEFAULT 0,
    status INT NOT NULL DEFAULT 1,
    last_login_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO user (username, password, email, role, status) VALUES
('admin', '6b8c32f80bc2269e481494e4547290cd', 'admin@library.com', 1, 1);

-- 分类表
CREATE TABLE categroy (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200),
    parent_id BIGINT,
    sort INT NOT NULL DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO categroy (name, description, sort) VALUES
('计算机科学', '计算机相关', 1),
('文学小说', '文学类', 2),
('历史传记', '历史类', 3),
('科学技术', '科技类', 4),
('哲学心理', '哲学类', 5);

-- 图书表
CREATE TABLE book (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(100) NOT NULL,
    publisher VARCHAR(100),
    publish_time DATE,
    categroy_id BIGINT,
    price DECIMAL(10,2) DEFAULT 0.00,
    stock INT NOT NULL DEFAULT 0,
    cover_url VARCHAR(500),
    description VARCHAR(500),
    status INT NOT NULL DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO book (isbn, title, author, publisher, price, stock, description) VALUES
('978-7-111-11111-1', 'Java编程思想', 'Bruce Eckel', '机械工业出版社', 108.00, 10, 'Java经典'),
('978-7-302-22222-2', 'Spring实战', 'Craig Walls', '人民邮电出版社', 89.00, 15, 'Spring指南'),
('978-7-115-33333-3', '深入理解计算机系统', 'Randal E. Bryant', '机械工业出版社', 139.00, 8, 'CS经典'),
('978-7-121-44444-4', '活着', '余华', '作家出版社', 29.00, 20, '文学经典'),
('978-7-542-55555-5', '三体', '刘慈欣', '重庆出版社', 62.00, 25, '科幻巨著');

-- 借阅记录表
CREATE TABLE borrow_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    borrow_time DATETIME NOT NULL,
    due_time DATETIME NOT NULL,
    return_time DATETIME,
    status INT NOT NULL DEFAULT 0,
    remark VARCHAR(200),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_book_id (book_id),
    INDEX idx_status (status),
    INDEX idx_due_time (due_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
