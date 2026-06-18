package com.pwenjie.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pwenjie.common.enums.UserRoleEnum;
import lombok.Data;

import java.util.Date;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String avatar;
    private Integer role;
    private Integer status;

    private String token;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastLoginTime;


    public String getRoleName(){
        UserRoleEnum roleEnum = UserRoleEnum.getByCode(this.role);
        return roleEnum != null ? roleEnum.getDesc() : "未知";
    }


    public String getStatusName(){
        if(status == null) return "未知";
        switch (status){
            case 0: return "禁用";
            case 1: return "启用";
            default: return "未知";
        }
    }

    public boolean isAdmin() {
        return UserRoleEnum.isAdmin(this.role);
    }
}
