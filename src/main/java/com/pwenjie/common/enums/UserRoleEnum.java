//TODO在这里存放用户状态枚举

package com.pwenjie.common.enums;

import com.pwenjie.common.constant.UserConstants;
import lombok.Getter;

@Getter
public enum UserRoleEnum {
    USER(UserConstants.ROLE_USER, "普通用户"),
    ADMIN(UserConstants.ROLE_ADMIN, "管理员");

    private final Integer code;
    private final String desc;

    UserRoleEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static UserRoleEnum getByCode(Integer code) {
        for(UserRoleEnum role : values()) {
            if(role.getCode().equals(code)) {
                return role;
            }
        }
        return null;
    }

    public static boolean isAdmin(Integer rolecode){
        return ADMIN.getCode().equals(rolecode);
    }
}
