package com.pwenjie.service;

import com.pwenjie.dto.request.BorrowBookDTO;
import com.pwenjie.dto.response.BorrowRecordVO;

import java.util.List;

public interface BorrowRecordService {

    BorrowRecordVO borrowBook(BorrowBookDTO borrowBookDTO);

    BorrowRecordVO returnBook(Long recordId);

    BorrowRecordVO getBorrowRecordById(Long id);

    List<BorrowRecordVO> getAllBorrowRecords();

    List<BorrowRecordVO> getBorrowRecordsByUserId(Long userId, Integer pageNum, Integer pageSize);

    List<BorrowRecordVO> getBorrowRecordsByBookId(Long bookId);

    List<BorrowRecordVO> getBorrowingRecords();

    List<BorrowRecordVO> getOverdueRecords();

    int getBorrowCount();
}
