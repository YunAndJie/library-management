package com.pwenjie.mapper;

import com.pwenjie.entity.BorrowRecord;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface BorrowRecordMapper {

    @Select("SELECT * FROM borrow_record WHERE id = #{id}")
    BorrowRecord selectById(@Param("id") Long id);

    @Select("SELECT * FROM borrow_record ORDER BY id DESC")
    List<BorrowRecord> selectAll();

    @Select("SELECT * FROM borrow_record WHERE user_id = #{userId} ORDER BY id DESC")
    List<BorrowRecord> selectByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM borrow_record WHERE book_id = #{bookId} ORDER BY id DESC")
    List<BorrowRecord> selectByBookId(@Param("bookId") Long bookId);

    @Select("SELECT * FROM borrow_record WHERE status = #{status} ORDER BY id DESC")
    List<BorrowRecord> selectByStatus(@Param("status") Integer status);

    @Select("SELECT * FROM borrow_record WHERE user_id = #{userId} ORDER BY id DESC LIMIT #{offset}, #{pageSize}")
    List<BorrowRecord> selectPageByUserId(@Param("userId") Long userId, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    @Select("SELECT COUNT(*) FROM borrow_record WHERE user_id = #{userId} AND status = 0")
    int countBorrowingByUserId(@Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM borrow_record WHERE user_id = #{userId} AND status = 2")
    int countOverdueByUserId(@Param("userId") Long userId);

    @Insert("INSERT INTO borrow_record(user_id, book_id, borrow_time, due_time, status, remark, create_time) " +
            "VALUES(#{userId}, #{bookId}, #{borrowTime}, #{dueTime}, #{status}, #{remark}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(BorrowRecord record);

    @Update("UPDATE borrow_record SET status = #{status}, return_time = #{returnTime}, remark = #{remark} WHERE id = #{id}")
    int updateReturnInfo(@Param("id") Long id, @Param("status") Integer status,
                         @Param("returnTime") Date returnTime, @Param("remark") String remark);

    @Select("SELECT COUNT(*) FROM borrow_record")
    int count();

    @Update("UPDATE borrow_record SET status = #{overdueStatus} WHERE status = 0 AND due_time < #{now}")
    int updateOverdueStatus(@Param("overdueStatus") Integer overdueStatus, @Param("now") Date now);
}
