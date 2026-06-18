package com.pwenjie.mapper;

import com.pwenjie.entity.Categroy;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategroyMapper {

    @Select("SELECT * FROM categroy WHERE id = #{id}")
    Categroy selectById(@Param("id") Long id);

    @Select("SELECT * FROM categroy ORDER BY sort ASC, id DESC")
    List<Categroy> selectAll();

    @Select("SELECT * FROM categroy WHERE parent_id = #{parentId} ORDER BY sort ASC, id DESC")
    List<Categroy> selectByParentId(@Param("parentId") Long parentId);

    @Select("SELECT * FROM categroy WHERE name = #{name}")
    Categroy selectByName(@Param("name") String name);

    @Insert("INSERT INTO categroy(name, description, parent_id, sort, create_time) " +
            "VALUES(#{name}, #{description}, #{parentId}, #{sort}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Categroy categroy);

    @Update("UPDATE categroy SET name = #{name}, description = #{description}, " +
            "parent_id = #{parentId}, sort = #{sort} WHERE id = #{id}")
    int update(Categroy categroy);

    @Delete("DELETE FROM categroy WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM categroy")
    int count();

    @Select("SELECT COUNT(*) FROM categroy WHERE parent_id = #{parentId}")
    int countByParentId(@Param("parentId") Long parentId);
}
