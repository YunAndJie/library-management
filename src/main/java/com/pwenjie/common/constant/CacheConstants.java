//TODO在这里存放缓存相关的常量


package com.pwenjie.common.constant;

public class CacheConstants {
    public static final String USER_TOKEN_PREFIX = "USER_TOKEN:";
    public static final long USER_TOKEN_EXPIRE = 30 * 60;  // 30分钟

    public static final String BOOK_PREFIX = "BOOK:";
    public static final String BOOK_LIST_PREFIX = "BOOK_LIST:";
    public static final long BOOK_EXPIRE = 10 * 60;  // 10分钟

    public static final String CAPTCHA_PREFIX = "CAPTCHA:";
    public static final long CAPTCHA_EXPIRE = 5 * 60;  // 5分钟

    public static final String RATE_LIMIT_PREFIX = "RATE_LIMIT:";
    public static final long RATE_LIMIT_EXPIRE = 1;  // 1秒

    public static final String CATEGORY_PREFIX = "CATEGORY:";
    public static final String CATEGORY_LIST_PREFIX = "CATEGORY_LIST:";
    public static final long CATEGORY_EXPIRE = 30 * 60;  // 30分钟

    public static final String BORROW_PREFIX = "BORROW:";
    public static final String BORROW_LIST_PREFIX = "BORROW_LIST:";
    public static final long BORROW_EXPIRE = 5 * 60;  // 5分钟
}
