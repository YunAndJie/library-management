//TODO在这里存放用户常量

package com.pwenjie.common.constant;

public class UserConstants {
    // 用户角色
    public static final int ROLE_USER = 0;
    public static final int ROLE_ADMIN = 1;

    // 用户状态
    public static final int STATUS_DISABLED = 0;
    public static final int STATUS_ENABLED = 1;

    // 验证规则
    public static final int USERNAME_MIN_LENGTH = 3;
    public static final int USERNAME_MAX_LENGTH = 20;
    public static final int PASSWORD_MIN_LENGTH = 6;
    public static final int PASSWORD_MAX_LENGTH = 20;
    public static final int EMAIL_MAX_LENGTH = 100;
    public static final int PHONE_LENGTH = 11;
    public static final int AVATAR_MAX_LENGTH = 500;

    // 正则表达式
    public static final String USERNAME_PATTERN = "^[a-zA-Z0-9_]+$";
    public static final String PASSWORD_PATTERN = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{6,20}$";
    public static final String PHONE_PATTERN = "^1[3-9]\\d{9}$";
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    // 密码盐值
    public static final String PASSWORD_SALT = "library_salt_2024";

    // 默认值
    public static final String DEFAULT_AVATAR = "";
    public static final String DEFAULT_PASSWORD = "123456";

    // 消息
    public static final String MSG_USERNAME_NOT_BLANK = "用户名不能为空";
    public static final String MSG_USERNAME_LENGTH = "用户名长度3-20位";
    public static final String MSG_USERNAME_PATTERN = "用户名只能包含字母、数字、下划线";
    public static final String MSG_PASSWORD_NOT_BLANK = "密码不能为空";
    public static final String MSG_PASSWORD_LENGTH = "密码长度6-20位";
    public static final String MSG_PASSWORD_PATTERN = "密码必须包含字母和数字";
    public static final String MSG_EMAIL_NOT_BLANK = "邮箱不能为空";
    public static final String MSG_EMAIL_INVALID = "邮箱格式不正确";
    public static final String MSG_PHONE_INVALID = "手机号格式不正确";
    public static final String MSG_CONFIRM_PASSWORD_NOT_BLANK = "确认密码不能为空";

    // Token相关
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
}
