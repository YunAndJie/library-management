package com.pwenjie.service;

import com.pwenjie.dto.request.BookAddDTO;
import com.pwenjie.dto.request.BookQueryDTO;
import com.pwenjie.dto.response.BookVO;

import java.util.List;

public interface BookService {

    BookVO addBook(BookAddDTO bookAddDTO);

    BookVO getBookById(Long id);

    List<BookVO> getAllBooks();

    List<BookVO> getBooksByPage(Integer pageNum, Integer pageSize);

    List<BookVO> searchBooks(String keyword);

    List<BookVO> getBooksByCategory(Long categoryId);

    BookVO updateBook(Long id, BookAddDTO bookAddDTO);

    boolean deleteBook(Long id);

    int getBookCount();
}
