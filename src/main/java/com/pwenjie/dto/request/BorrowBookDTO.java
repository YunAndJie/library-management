package com.pwenjie.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BorrowBookDTO {

    @NotNull(message = "用户ID不能为空")
    @Min(value = 1, message = "用户ID不合法")
    private Long userId;

    @NotNull(message = "图书ID不能为空")
    @Min(value = 1, message = "图书ID不合法")
    private Long bookId;

    private Integer borrowDays = 30;
}
