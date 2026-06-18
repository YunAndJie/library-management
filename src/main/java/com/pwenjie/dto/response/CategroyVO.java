package com.pwenjie.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CategroyVO {
    private Long id;
    private String name;
    private String description;
    private Long parentId;
    private Integer sort;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private List<CategroyVO> children;
}
