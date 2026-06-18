package com.pwenjie.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BookVO {
    private Long id;
    private String isbn;
    private String title;
    private String author;
    private String publisher;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date publishTime;

    private Long categroyId;
    private BigDecimal price;
    private Integer stock;
    private String coverUrl;
    private String description;
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public String getStatusName() {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "下架";
            case 1: return "在架";
            case 3: return "已借阅";
            default: return "未知";
        }
    }
}
