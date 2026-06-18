-- 删除旧表（会丢失数据，仅用于开发环境）
DROP TABLE IF EXISTS borrow_record;
DROP TABLE IF EXISTS book;
DROP TABLE IF EXISTS categroy;
DROP TABLE IF EXISTS user;

-- 重新建表
source library_init.sql;
