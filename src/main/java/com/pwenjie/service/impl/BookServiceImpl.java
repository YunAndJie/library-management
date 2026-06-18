package com.pwenjie.service.impl;

import com.pwenjie.common.constant.CacheConstants;
import com.pwenjie.common.enums.BookStatusEnum;
import com.pwenjie.common.enums.ResponseCodeEnum;
import com.pwenjie.common.exception.BusinessException;
import com.pwenjie.dto.request.BookAddDTO;
import com.pwenjie.dto.response.BookVO;
import com.pwenjie.entity.Book;
import com.pwenjie.mapper.BookMapper;
import com.pwenjie.service.BookService;
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
public class BookServiceImpl implements BookService {

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public BookVO addBook(BookAddDTO bookAddDTO) {
        Book book = new Book();
        BeanUtils.copyProperties(bookAddDTO, book);
        book.setStatus(BookStatusEnum.ONLINE.getCode());
        book.setCreateTime(new Date());

        int result = bookMapper.insert(book);
        if (result <= 0) {
            throw new BusinessException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.getCode(), "添加图书失败");
        }

        clearBookListCache();
        return convertToVO(book);
    }

    @Override
    public BookVO getBookById(Long id) {
        String cacheKey = CacheConstants.BOOK_PREFIX + id;
        BookVO cached = (BookVO) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new BusinessException(ResponseCodeEnum.BOOK_NOT_EXIST);
        }

        BookVO bookVO = convertToVO(book);
        redisTemplate.opsForValue().set(cacheKey, bookVO, CacheConstants.BOOK_EXPIRE, TimeUnit.SECONDS);
        return bookVO;
    }

    @Override
    public List<BookVO> getAllBooks() {
        String cacheKey = CacheConstants.BOOK_LIST_PREFIX + "all";
        @SuppressWarnings("unchecked")
        List<BookVO> cached = (List<BookVO>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        List<Book> books = bookMapper.selectAll();
        List<BookVO> bookVOs = books.stream().map(this::convertToVO).collect(Collectors.toList());
        redisTemplate.opsForValue().set(cacheKey, bookVOs, CacheConstants.BOOK_EXPIRE, TimeUnit.SECONDS);
        return bookVOs;
    }

    @Override
    public List<BookVO> getBooksByPage(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;

        String cacheKey = CacheConstants.BOOK_LIST_PREFIX + "page:" + pageNum + ":" + pageSize;
        @SuppressWarnings("unchecked")
        List<BookVO> cached = (List<BookVO>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        int offset = (pageNum - 1) * pageSize;
        List<Book> books = bookMapper.selectPage(offset, pageSize);
        List<BookVO> bookVOs = books.stream().map(this::convertToVO).collect(Collectors.toList());
        redisTemplate.opsForValue().set(cacheKey, bookVOs, CacheConstants.BOOK_EXPIRE, TimeUnit.SECONDS);
        return bookVOs;
    }

    @Override
    public List<BookVO> searchBooks(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return getAllBooks();
        }

        String cacheKey = CacheConstants.BOOK_LIST_PREFIX + "search:" + keyword;
        @SuppressWarnings("unchecked")
        List<BookVO> cached = (List<BookVO>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        List<Book> books = bookMapper.search(keyword);
        List<BookVO> bookVOs = books.stream().map(this::convertToVO).collect(Collectors.toList());
        redisTemplate.opsForValue().set(cacheKey, bookVOs, CacheConstants.BOOK_EXPIRE, TimeUnit.SECONDS);
        return bookVOs;
    }

    @Override
    public List<BookVO> getBooksByCategory(Long categoryId) {
        String cacheKey = CacheConstants.BOOK_LIST_PREFIX + "category:" + categoryId;
        @SuppressWarnings("unchecked")
        List<BookVO> cached = (List<BookVO>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        List<Book> books = bookMapper.selectByCategoryId(categoryId);
        List<BookVO> bookVOs = books.stream().map(this::convertToVO).collect(Collectors.toList());
        redisTemplate.opsForValue().set(cacheKey, bookVOs, CacheConstants.BOOK_EXPIRE, TimeUnit.SECONDS);
        return bookVOs;
    }

    @Override
    public BookVO updateBook(Long id, BookAddDTO bookAddDTO) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new BusinessException(ResponseCodeEnum.BOOK_NOT_EXIST);
        }

        BeanUtils.copyProperties(bookAddDTO, book, "id", "status", "createTime");
        int result = bookMapper.update(book);
        if (result <= 0) {
            throw new BusinessException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.getCode(), "更新图书失败");
        }

        redisTemplate.delete(CacheConstants.BOOK_PREFIX + id);
        clearBookListCache();
        return getBookById(id);
    }

    @Override
    public boolean deleteBook(Long id) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new BusinessException(ResponseCodeEnum.BOOK_NOT_EXIST);
        }

        int result = bookMapper.deleteById(id);
        if (result > 0) {
            redisTemplate.delete(CacheConstants.BOOK_PREFIX + id);
            clearBookListCache();
            return true;
        }
        return false;
    }

    @Override
    public int getBookCount() {
        return bookMapper.count();
    }

    private BookVO convertToVO(Book book) {
        if (book == null) return null;
        BookVO bookVO = new BookVO();
        BeanUtils.copyProperties(book, bookVO);
        return bookVO;
    }

    private void clearBookListCache() {
        redisTemplate.delete(CacheConstants.BOOK_LIST_PREFIX + "all");
        // 模糊删除所有列表缓存
        var keys = redisTemplate.keys(CacheConstants.BOOK_LIST_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
