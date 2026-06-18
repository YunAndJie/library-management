package com.pwenjie.entity;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String avatar; //头像URL
    private Integer role;
    private Integer status;
    private Date lastLoginTime;
    private Date createTime;
    private Date updateTime;
}
