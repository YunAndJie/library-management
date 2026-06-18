package com.pwenjie.service;

import com.pwenjie.dto.request.UserLoginDTO;
import com.pwenjie.dto.request.UserRegisterDTO;
import com.pwenjie.dto.request.UserUpdateDTO;
import com.pwenjie.dto.response.UserVO;
import com.pwenjie.entity.User;

import java.util.List;

public interface UserService {

    UserVO register(UserRegisterDTO userRegisterDTO);

    UserVO login(UserLoginDTO userLoginDTO);

    List<UserVO> getAllUsers();

    List<UserVO> getUserByPage(Integer pageNum, Integer pageSize);

    UserVO getUserById(Long id);

    UserVO getUserByUserName(String username);

    void logout(String token);

    UserVO updateUser(Long id, UserUpdateDTO userUpdateDTO);

    boolean updateUserStauts(Long id, Integer status);

    boolean deleteUser(Long id);

    int getUserCount();

}
