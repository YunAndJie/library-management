//TODO在这里存放全局异常处理器

package com.pwenjie.common.exception;

import com.pwenjie.common.enums.ResponseCodeEnum;
import com.pwenjie.common.result.Result;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;
import java.util.Set;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler{

    //处理业务异常
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e,
                                               HttpServletRequest request){
        log.warn("业务异常: {}, 请求路径: {}", e.getMessage(), request.getRequestURI());
        return Result.error(e.getCode(), e.getMessage());
    }

    //处理参数校验异常（@Validated在Controller参数上）
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e){
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        StringBuilder sb = new StringBuilder();
        for(ConstraintViolation<?> violation : violations){
            sb.append(violation.getMessage()).append(";");
        }

        log.warn("参数校验异常：{}", sb.toString());
        return Result.error(ResponseCodeEnum.BAD_REQUEST.getCode(), sb.toString());
    }

    //处理参数校验异常（@Valid在RequestBody上）
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        StringBuilder sb = new StringBuilder();
        for(FieldError fieldError : fieldErrors){
            sb.append(fieldError.getField())
                    .append(":")
                    .append(fieldError.getDefaultMessage())
                    .append(";");
        }

        log.warn("参数校验异常: {}", sb.toString());
        return Result.error(ResponseCodeEnum.BAD_REQUEST.getCode(), sb.toString());
    }

    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        StringBuilder sb = new StringBuilder();
        for (FieldError error : fieldErrors) {
            sb.append(error.getField())
                    .append(": ")
                    .append(error.getDefaultMessage())
                    .append("; ");
        }
        return Result.error(ResponseCodeEnum.BAD_REQUEST.getCode(), sb.toString());
    }

    //处理404异常
    @ExceptionHandler(NoHandlerFoundException.class)
    public Result<Void> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.warn("接口不存在: {}", e.getRequestURL());
        return Result.error(ResponseCodeEnum.NOT_FOUND);
    }


    //处理其他所有异常
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e,
                                        HttpServletRequest request){
        log.error("系统异常, 请求路径: {}, 异常: {}", request.getRequestURI(), e.getMessage(), e);
        return Result.error(ResponseCodeEnum.INTERNAL_SERVER_ERROR);
    }
}
