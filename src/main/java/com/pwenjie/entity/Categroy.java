package com.pwenjie.entity;


import lombok.Data;

import java.util.Date;

@Data
public class Categroy {
    private Long id;
    private String name;
    private String description;
    private Long parentId;
    private Integer sort; //排序
    private Date createTime;
}
