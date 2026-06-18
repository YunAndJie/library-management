package com.pwenjie.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class BorrowRecordVO {
    private Long id;
    private Long userId;
    private String username;
    private Long bookId;
    private String bookTitle;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date borrowTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dueTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date returnTime;

    private Integer status;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private Boolean overdue;

    public String getStatusName() {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "借阅中";
            case 1: return "已归还";
            case 2: return "已超期";
            default: return "未知";
        }
    }
}
