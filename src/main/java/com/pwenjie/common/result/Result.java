//TODO在这里是统一响应封装


package com.pwenjie.common.result;

import com.pwenjie.common.enums.ResponseCodeEnum;
import lombok.Data;

import java.io.Serializable;



@Data
public class Result<T> implements Serializable {
    private Integer code;  //状态码
    private String message;//消息
    private T data;        //数据
    private Long timestamp = System.currentTimeMillis(); //时间戳

    public Result() {}

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    //成功
    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data){
        return new Result<>(ResponseCodeEnum.SUCCESS.getCode(), ResponseCodeEnum.SUCCESS.getMessage(), data);
    }

    public static <T> Result<T> success(T data, String message){
        return new Result<>(ResponseCodeEnum.SUCCESS.getCode(), message, data);
    }

    public static <T> Result<T> error(String message) {
        return error(ResponseCodeEnum.INTERNAL_SERVER_ERROR.getCode(), message);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> error(ResponseCodeEnum responseCode) {
        return new Result<>(responseCode.getCode(), responseCode.getMessage(), null);
    }

}
