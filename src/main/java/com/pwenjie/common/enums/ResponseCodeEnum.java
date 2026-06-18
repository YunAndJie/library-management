//TODO在这里存放消息响应枚举

package com.pwenjie.common.enums;

import lombok.Getter;

@Getter
public enum ResponseCodeEnum {
    SUCCESS(200, "操作成功"),

    //客户端错误
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权访问"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),

    //业务错误_用户模块
    USER_NOT_EXIST(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "密码错误"),
    USER_EXISTS(1003, "用户已存在"),
    USER_DISABLED(1004, "用户已被禁用"),

    //业务错误_图书模块
    BOOK_NOT_EXIST(2001, "图书不存在"),
    BOOK_OUT_OF_STOCK(2002, "图书库存不足"),
    BOOK_STATUS_ERROR(2003, "图书状态异常"),

    // 业务错误 - 借阅模块
    BORROW_RECORD_NOT_EXIST(3001, "借阅记录不存在"),
    BORROW_OVER_LIMIT(3002, "借阅数量已达上限"),
    BORROW_OVERDUE(3003, "存在超期未还图书"),

    // 系统错误
    INTERNAL_SERVER_ERROR(500, "系统内部错误"),
    SERVICE_UNAVAILABLE(503, "服务暂不可用");

    private final Integer code;
    private final String message;

    ResponseCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ResponseCodeEnum getResponseCodeEnum(Integer code) {
        for (ResponseCodeEnum responseCodeEnum : values()) {
            if(responseCodeEnum.getCode().equals(code)) {
                return responseCodeEnum;
            }
        }
        return INTERNAL_SERVER_ERROR;
    }
}
