package com.pwenjie.dto.request;

import lombok.Data;

@Data
public class BookQueryDTO {

    private String keyword;

    private Long categoryId;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
