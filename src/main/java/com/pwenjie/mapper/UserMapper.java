package com.pwenjie.mapper;


import com.pwenjie.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {


    //查询所有
    @Select("SELECT * FROM user ORDER BY id DESC")
    List<User> selectAll();

    //根据id查询
    @Select("SELECT * FROM user where id = #{id}")
    User selectById(@Param("id") Long id);

    //根据用户名查询
    @Select("SELECT * FROM user where username = #{username}")
    User selectByUsername(@Param("username") String username);

    //插入用户
    @Insert("INSERT INTO user(username, password, email, phone, role, status)" +
            "VALUES(#{username},#{password}, #{email}, #{phone}, #{role}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    //更新用户信息
    @Update("UPDATE user SET email = #{email}, phone = #{phone}, avatar = #{avatar}, " +
            "update_time = NOW() WHERE id = #{id}")
    int update(User user);

    //更新用户状态
    @Update("UPDATE user SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    //更新最后登录时间
    @Update("UPDATE user SET last_login_time = NOW() WHERE id = #{id}")
    int updatelastLoginTime(@Param("id") Long id);

    //逻辑删除用户
    @Delete("DELETE FROM user WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    //统计用户数量
    @Select("SELECT COUNT(*) FROM user")
    int count();

    //分页查询用户
    @Select("SELECT * FROM user ORDER BY id DESC LIMIT #{offset}, #{pageSize}")
    List<User> selectPage(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize);
}
