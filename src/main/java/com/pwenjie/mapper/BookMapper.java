package com.pwenjie.mapper;

import com.pwenjie.entity.Book;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BookMapper {

    @Select("SELECT * FROM book WHERE id = #{id}")
    Book selectById(@Param("id") Long id);

    @Select("SELECT * FROM book ORDER BY id DESC")
    List<Book> selectAll();

    @Select("SELECT * FROM book ORDER BY id DESC LIMIT #{offset}, #{pageSize}")
    List<Book> selectPage(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    @Select("SELECT * FROM book WHERE title LIKE CONCAT('%', #{keyword}, '%') OR author LIKE CONCAT('%', #{keyword}, '%') ORDER BY id DESC")
    List<Book> search(@Param("keyword") String keyword);

    @Select("SELECT * FROM book WHERE categroy_id = #{categoryId} ORDER BY id DESC")
    List<Book> selectByCategoryId(@Param("categoryId") Long categoryId);

    @Insert("INSERT INTO book(isbn, title, author, publisher, publish_time, categroy_id, price, stock, cover_url, description, status, create_time) " +
            "VALUES(#{isbn}, #{title}, #{author}, #{publisher}, #{publishTime}, #{categroyId}, #{price}, #{stock}, #{coverUrl}, #{description}, #{status}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Book book);

    @Update("UPDATE book SET isbn = #{isbn}, title = #{title}, author = #{author}, publisher = #{publisher}, " +
            "publish_time = #{publishTime}, categroy_id = #{categroyId}, price = #{price}, stock = #{stock}, " +
            "cover_url = #{coverUrl}, description = #{description}, status = #{status}, update_time = NOW() WHERE id = #{id}")
    int update(Book book);

    @Update("UPDATE book SET stock = stock - 1 WHERE id = #{id} AND stock > 0")
    int decreaseStock(@Param("id") Long id);

    @Update("UPDATE book SET stock = stock + 1 WHERE id = #{id}")
    int increaseStock(@Param("id") Long id);

    @Delete("DELETE FROM book WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM book")
    int count();
}
