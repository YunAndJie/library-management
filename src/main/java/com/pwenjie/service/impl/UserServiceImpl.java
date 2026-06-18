package com.pwenjie.service.impl;

import com.pwenjie.common.constant.CacheConstants;
import com.pwenjie.common.constant.UserConstants;
import com.pwenjie.common.enums.ResponseCodeEnum;
import com.pwenjie.common.exception.BusinessException;
import com.pwenjie.common.utils.JwtUtil;
import com.pwenjie.common.utils.Md5Util;
import com.pwenjie.common.utils.ValiDateUtil;
import com.pwenjie.dto.request.UserLoginDTO;
import com.pwenjie.dto.request.UserRegisterDTO;
import com.pwenjie.dto.request.UserUpdateDTO;
import com.pwenjie.dto.response.UserVO;
import com.pwenjie.entity.User;
import com.pwenjie.mapper.UserMapper;
import com.pwenjie.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public UserVO register(UserRegisterDTO userRegisterDTO){
        if(!userRegisterDTO.isPasswordMatch()){
            throw new BusinessException(ResponseCodeEnum.BAD_REQUEST.getCode(),
                    "两次输入的密码不一样");
        }

        if(!ValiDateUtil.isUsername(userRegisterDTO.getUsername())){
            throw new BusinessException(ResponseCodeEnum.BAD_REQUEST.getCode(),
                    UserConstants.MSG_USERNAME_PATTERN);
        }

        if (!ValiDateUtil.isEmail(userRegisterDTO.getEmail())) {
            throw new BusinessException(ResponseCodeEnum.BAD_REQUEST.getCode(),
                    UserConstants.MSG_EMAIL_INVALID);
        }

//        if (StringUtils.hasText(userRegisterDTO.getPhone()) &&
//                !ValiDateUtil.isPhone(userRegisterDTO.getPhone())) {
//            throw new BusinessException(ResponseCodeEnum.BAD_REQUEST.getCode(),
//                    UserConstants.MSG_PHONE_INVALID);
//        }

        User existUser = userMapper.selectByUsername(userRegisterDTO.getUsername());
        if (existUser != null) {
            throw new BusinessException(ResponseCodeEnum.USER_EXISTS);
        }

        User user = new User();
        BeanUtils.copyProperties(userRegisterDTO,user);

        //MD5加密
        String encryptedPassword = Md5Util.md5WithSalt(userRegisterDTO.getPassword());
        user.setPassword(encryptedPassword);


        user.setAvatar(UserConstants.DEFAULT_AVATAR);
        user.setRole(UserConstants.ROLE_USER);
        user.setStatus(UserConstants.STATUS_ENABLED);
        user.setCreateTime(new Date());

        int reuslt = userMapper.insert(user);
        if(reuslt < 0){
            throw new BusinessException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.getCode(), "注册失败");
        }

        return convertToVO(user);
    }

    @Override
    public UserVO login(UserLoginDTO  userLoginDTO){
        User user = userMapper.selectByUsername(userLoginDTO.getUsername());
        if(user == null){
            throw new BusinessException(ResponseCodeEnum.USER_PASSWORD_ERROR.getCode(),
                    "用户名或者密码错误");
        }

        if(!Md5Util.verify(userLoginDTO.getPassword(), user.getPassword())){
            throw new BusinessException(ResponseCodeEnum.USER_PASSWORD_ERROR);
        }

        if (user.getStatus() != null && user.getStatus() == UserConstants.STATUS_DISABLED) {
            throw new BusinessException(ResponseCodeEnum.USER_DISABLED);
        }

        user.setLastLoginTime(new Date());
        userMapper.update(user);

        // 生成JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 将Token存入Redis
        String redisKey = CacheConstants.USER_TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(redisKey, user.getId(), CacheConstants.USER_TOKEN_EXPIRE, TimeUnit.SECONDS);

        UserVO userVO = convertToVO(user);
        userVO.setToken(token);
        return userVO;
    }

    @Override
    public List<UserVO> getAllUsers() {
        List<User> users = userMapper.selectAll();
        return users.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserVO> getUserByPage(Integer pageNum, Integer pageSize) {
        if(pageNum == null || pageNum< 1){
            pageNum = 1;
        }
        if(pageSize == null || pageSize < 1){
            pageSize = 10;
        }

        int offset = (pageNum - 1) * pageSize;
        List<User> users = userMapper.selectPage(offset,pageSize);
        return users.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public UserVO getUserById(Long id) {
        User user = userMapper.selectById(id);
        if(user == null){
            throw new BusinessException(ResponseCodeEnum.USER_NOT_EXIST);
        }
        return convertToVO(user);
    }

    @Override
    public UserVO getUserByUserName(String username) {
        User user = userMapper.selectByUsername(username);
        if(user == null){
            throw new BusinessException(ResponseCodeEnum.USER_NOT_EXIST);
        }
        return convertToVO(user);
    }

    @Override
    public UserVO updateUser(Long id, UserUpdateDTO userUpdateDTO) {
        User user = userMapper.selectById(id);
        if(user == null){
            throw new BusinessException(ResponseCodeEnum.USER_NOT_EXIST);
        }

        if(StringUtils.hasText(userUpdateDTO.getEmail())){
            if (!ValiDateUtil.isEmail(userUpdateDTO.getEmail())) {
                throw new BusinessException(ResponseCodeEnum.BAD_REQUEST.getCode(),
                        UserConstants.MSG_EMAIL_INVALID);
            }
            user.setEmail(userUpdateDTO.getEmail());
        }

        if(StringUtils.hasText(userUpdateDTO.getPhone())){
            if (!ValiDateUtil.isPhone(userUpdateDTO.getPhone())) {
                throw new BusinessException(ResponseCodeEnum.BAD_REQUEST.getCode(),
                        UserConstants.MSG_PHONE_INVALID);
            }
            user.setPhone(userUpdateDTO.getPhone());
        }
        if(userUpdateDTO.getAvatar() != null){
            user.setAvatar(userUpdateDTO.getAvatar());
        }

        int result = userMapper.update(user);
        if(result <= 0){
            throw new BusinessException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.getCode(),
                    "更新失败");
        }

        return convertToVO(user);
    }

    @Override
    public boolean updateUserStauts(Long id, Integer status) {
        if (status != UserConstants.STATUS_DISABLED && status != UserConstants.STATUS_ENABLED) {
            throw new BusinessException(ResponseCodeEnum.BAD_REQUEST.getCode(),
                    "状态值不合法");
        }

        User user = userMapper.selectById(id);
        if(user == null){
            throw new BusinessException(ResponseCodeEnum.USER_NOT_EXIST);
        }

        int result = userMapper.updateStatus(id, status);
        return result > 0;
    }

    @Override
    public boolean deleteUser(Long id) {
        User user = userMapper.selectById(id);
        if(user == null){
            throw new BusinessException(ResponseCodeEnum.USER_NOT_EXIST);
        }

        int result = userMapper.deleteById(id);
        return result > 0;
    }

    @Override
    public void logout(String token) {
        if (StringUtils.hasText(token)) {
            String redisKey = CacheConstants.USER_TOKEN_PREFIX + token;
            redisTemplate.delete(redisKey);
        }
    }

    @Override
    public int getUserCount() {
        return userMapper.count();
    }

    private UserVO convertToVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

}
