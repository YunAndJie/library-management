package com.pwenjie.controller;

import com.pwenjie.aop.RateLimit;
import com.pwenjie.common.result.Result;
import com.pwenjie.dto.request.BorrowBookDTO;
import com.pwenjie.dto.response.BorrowRecordVO;
import com.pwenjie.service.BorrowRecordService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/borrow")
@Validated
public class BorrowController {

    @Autowired
    private BorrowRecordService borrowRecordService;

    @PostMapping
    @RateLimit(maxRequests = 3, windowSeconds = 60)
    public Result<BorrowRecordVO> borrowBook(@Valid @RequestBody BorrowBookDTO borrowBookDTO) {
        BorrowRecordVO vo = borrowRecordService.borrowBook(borrowBookDTO);
        return Result.success(vo, "借阅成功");
    }

    @PutMapping("/{id}/return")
    @RateLimit(maxRequests = 5, windowSeconds = 60)
    public Result<BorrowRecordVO> returnBook(@PathVariable @Min(1) Long id) {
        BorrowRecordVO vo = borrowRecordService.returnBook(id);
        return Result.success(vo, "归还成功");
    }

    @GetMapping("/{id}")
    public Result<BorrowRecordVO> getBorrowRecordById(@PathVariable @Min(1) Long id) {
        BorrowRecordVO vo = borrowRecordService.getBorrowRecordById(id);
        return Result.success(vo);
    }

    @GetMapping
    public Result<List<BorrowRecordVO>> getAllBorrowRecords() {
        List<BorrowRecordVO> vos = borrowRecordService.getAllBorrowRecords();
        return Result.success(vos);
    }

    @GetMapping("/user/{userId}")
    public Result<List<BorrowRecordVO>> getBorrowRecordsByUserId(
            @PathVariable @Min(1) Long userId,
            @RequestParam(defaultValue = "1") @Min(1) Integer pageNum,
            @RequestParam(defaultValue = "10") @Min(1) Integer pageSize) {
        List<BorrowRecordVO> vos = borrowRecordService.getBorrowRecordsByUserId(userId, pageNum, pageSize);
        return Result.success(vos);
    }

    @GetMapping("/book/{bookId}")
    public Result<List<BorrowRecordVO>> getBorrowRecordsByBookId(@PathVariable @Min(1) Long bookId) {
        List<BorrowRecordVO> vos = borrowRecordService.getBorrowRecordsByBookId(bookId);
        return Result.success(vos);
    }

    @GetMapping("/borrowing")
    public Result<List<BorrowRecordVO>> getBorrowingRecords() {
        List<BorrowRecordVO> vos = borrowRecordService.getBorrowingRecords();
        return Result.success(vos);
    }

    @GetMapping("/overdue")
    public Result<List<BorrowRecordVO>> getOverdueRecords() {
        List<BorrowRecordVO> vos = borrowRecordService.getOverdueRecords();
        return Result.success(vos);
    }

    @GetMapping("/count")
    public Result<Integer> getBorrowCount() {
        Integer count = borrowRecordService.getBorrowCount();
        return Result.success(count);
    }
}
