//TODO在这里存放书籍状态枚举




package com.pwenjie.common.enums;

import lombok.Getter;

@Getter
public enum BookStatusEnum {
    OFFLINE(0, "下架"),
    ONLINE(1, "在架"),
    BORROWED(3, "已借阅");

    private final Integer code;
    private final String dsc;

    BookStatusEnum(Integer code, String dsc) {
        this.code = code;
        this.dsc = dsc;
    }

    public static BookStatusEnum getByCode(Integer code){
        for(BookStatusEnum status : values()){
            if(status.getCode().equals(code)){
                return status;
            }
        }
        return null;
    }
}
