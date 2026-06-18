package com.pwenjie.entity;

import lombok.Data;

import java.util.Date;

@Data
public class BorrowRecord {
    private Long id;
    private Long userId;
    private Long bookId;
    private Date borrowTime;
    private Date dueTime;
    private Date returnTime;
    private Integer status;
    private String remark;
    private Date createTime;
}
