//TODO在这里存放借阅状态枚举

package com.pwenjie.common.enums;


import lombok.Getter;

@Getter
public enum BorrowStatusEnum {
    BORROWING(0, "借阅中"),
    RETURNED(1, "已归还"),
    OVERDUE(2, "已超期");

    private final Integer code;
    private final String desc;

    BorrowStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static BorrowStatusEnum getByCode(Integer code){
        for(BorrowStatusEnum status : values()){
            if(status.getCode().equals(code)){
                return status;
            }
        }
        return null;
    }
}
