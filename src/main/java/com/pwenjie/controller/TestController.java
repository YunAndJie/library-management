package com.pwenjie.controller;

import com.pwenjie.common.exception.BusinessException;
import com.pwenjie.common.result.Result;
import com.pwenjie.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/hello")
    public Result<String> hello(){
        return Result.success("hello!");
    }


    @GetMapping("/info")
    public Result<Object> getSystemInfo(){
        return Result.success(new Object(){
            public final String projectName = "在线图书管理系统";
            public final String version = "1.0.0";
            public final String author = "PWJ";
            public final String status = "开发中";
        });
    }



    @GetMapping("/test-error")
    public Result<String> testError(){
        throw new BusinessException("这是一个测试的业务异常");
    }


    @GetMapping("/test-system-error")
    public Result<String> testSystemError(){
        int i = 1/0;
        return Result.success("不执行");
    }



    @Autowired
    private TestService testService;

    @GetMapping("/db-test")
    public Result<Integer> testDatabase() {
        Integer result = testService.testDbConnection();
        if (result == 1) {
            return Result.success(1, "数据库连接成功");
        } else {
            return Result.error("数据库连接失败");
        }
    }



    @GetMapping("/generate-password/{password}")
    public Result<String> generatePassword(@PathVariable String password) {
        String encrypted = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        return Result.success(encrypted);
    }
}
