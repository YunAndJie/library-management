package com.pwenjie.controller;

import com.pwenjie.aop.RateLimit;
import com.pwenjie.common.result.Result;
import com.pwenjie.dto.request.BookAddDTO;
import com.pwenjie.dto.request.BookQueryDTO;
import com.pwenjie.dto.response.BookVO;
import com.pwenjie.service.BookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@Validated
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping
    @RateLimit(maxRequests = 5, windowSeconds = 60)
    public Result<BookVO> addBook(@Valid @RequestBody BookAddDTO bookAddDTO) {
        BookVO bookVO = bookService.addBook(bookAddDTO);
        return Result.success(bookVO, "添加图书成功");
    }

    @GetMapping("/{id}")
    public Result<BookVO> getBookById(@PathVariable @Min(1) Long id) {
        BookVO bookVO = bookService.getBookById(id);
        return Result.success(bookVO);
    }

    @GetMapping
    public Result<List<BookVO>> getAllBooks() {
        List<BookVO> bookVOs = bookService.getAllBooks();
        return Result.success(bookVOs);
    }

    @GetMapping("/page")
    public Result<List<BookVO>> getBooksByPage(
            @RequestParam(defaultValue = "1") @Min(1) Integer pageNum,
            @RequestParam(defaultValue = "10") @Min(1) Integer pageSize) {
        List<BookVO> bookVOs = bookService.getBooksByPage(pageNum, pageSize);
        return Result.success(bookVOs);
    }

    @GetMapping("/search")
    public Result<List<BookVO>> searchBooks(@RequestParam String keyword) {
        List<BookVO> bookVOs = bookService.searchBooks(keyword);
        return Result.success(bookVOs);
    }

    @GetMapping("/category/{categoryId}")
    public Result<List<BookVO>> getBooksByCategory(@PathVariable @Min(1) Long categoryId) {
        List<BookVO> bookVOs = bookService.getBooksByCategory(categoryId);
        return Result.success(bookVOs);
    }

    @PutMapping("/{id}")
    @RateLimit(maxRequests = 5, windowSeconds = 60)
    public Result<BookVO> updateBook(
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody BookAddDTO bookAddDTO) {
        BookVO bookVO = bookService.updateBook(id, bookAddDTO);
        return Result.success(bookVO, "更新图书成功");
    }

    @DeleteMapping("/{id}")
    @RateLimit(maxRequests = 5, windowSeconds = 60)
    public Result<Void> deleteBook(@PathVariable @Min(1) Long id) {
        boolean success = bookService.deleteBook(id);
        if (success) {
            return Result.success(null, "删除成功");
        } else {
            return Result.error("删除失败");
        }
    }

    @GetMapping("/count")
    public Result<Integer> getBookCount() {
        Integer count = bookService.getBookCount();
        return Result.success(count);
    }
}
