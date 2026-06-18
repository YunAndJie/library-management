//TODO在这里存放自定义业务异常


package com.pwenjie.common.exception;


import com.pwenjie.common.enums.ResponseCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException{
    private Integer code;

    public BusinessException(String message){
        super(message);
        this.code = 500;
    }

    public BusinessException(Integer code, String message){
        super(message);
        this.code = code;
    }

    public BusinessException(ResponseCodeEnum responseCode) {
        super(responseCode.getMessage());
        this.code = responseCode.getCode();
    }

    public BusinessException(ResponseCodeEnum responseCode, String message) {
        super(message);
        this.code = responseCode.getCode();
    }
}
