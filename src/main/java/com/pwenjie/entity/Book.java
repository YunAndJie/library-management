package com.pwenjie.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


@Data
public class Book {
    private Long id;
    private String isbn; //书籍号
    private String title; //名字
    private String author; //作者
    private String publisher; //出版社
    private Date publishTime; //出版日期
    private Long categroyId;
    private BigDecimal price;
    private Integer stock; //库存
    private String coverUrl; //封面URL
    private String description; //描述
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
