package com.pwenjie.controller;


import com.pwenjie.common.result.Result;
import com.pwenjie.dto.request.UserLoginDTO;
import com.pwenjie.dto.request.UserRegisterDTO;
import com.pwenjie.dto.request.UserUpdateDTO;
import com.pwenjie.dto.response.UserVO;
import com.pwenjie.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        UserVO userVO = userService.register(userRegisterDTO);
        return Result.success(userVO, "注册成功");
    }

    @PostMapping("/login")
    public Result<UserVO> login(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        UserVO userVO = userService.login(userLoginDTO);
        return Result.success(userVO, "登录成功");
    }

    @GetMapping
    public Result<List<UserVO>> getAllUsers(){
        List<UserVO> userVOs = userService.getAllUsers();
        return Result.success(userVOs);
    }

    @GetMapping("/page")
    public Result<List<UserVO>> getUsersByPage(
            @RequestParam(defaultValue = "1") @Min(1) Integer pageNum,
            @RequestParam(defaultValue = "10")@Min(1) Integer pageSize)
    {
        List<UserVO> userVOs = userService.getUserByPage(pageNum, pageSize);
        return Result.success(userVOs);
    }

    @GetMapping("/{id}")
    public Result<UserVO> getUserById(@PathVariable @Min(1) Long id){
        UserVO userVO = userService.getUserById(id);
        return  Result.success(userVO);
    }

    @GetMapping("/username/{username}")
    public Result<UserVO> getUserByUsername(@PathVariable String username){
        UserVO userVO = userService.getUserByUserName(username);
        return  Result.success(userVO);
    }

    @PutMapping("/{id}")
    public Result<UserVO> updateUser(
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody UserUpdateDTO  userUpdateDTO)
    {
        UserVO userVO = userService.updateUser(id, userUpdateDTO);
        return  Result.success(userVO, "更新成功");
    }

    @PutMapping("/{id}/status/{status}")
    public Result<Void> updateUserStauts(
            @PathVariable @Min(1) Long id,
            @PathVariable @Min(0) Integer status)
    {
        boolean success = userService.updateUserStauts(id, status);
        if(success){
            return  Result.success(null, "状态更新成功");
        }else{
            return Result.error("状态更新失败");
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable @Min(1) Long id){
        boolean success = userService.deleteUser(id);
        if(success){
            return Result.success(null, "删除成功");
        }else{
            return Result.error("删除失败");
        }
    }

    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String authHeader) {
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        userService.logout(token);
        return Result.success(null, "退出成功");
    }

    @GetMapping("/count")
    public Result<Integer> getUsercount(){
        Integer count = userService.getUserCount();
        return  Result.success(count);
    }


}
