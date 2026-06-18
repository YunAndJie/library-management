package com.pwenjie.service.impl;

import com.pwenjie.common.constant.CacheConstants;
import com.pwenjie.common.constant.SystemConstants;
import com.pwenjie.common.enums.BookStatusEnum;
import com.pwenjie.common.enums.BorrowStatusEnum;
import com.pwenjie.common.enums.ResponseCodeEnum;
import com.pwenjie.common.exception.BusinessException;
import com.pwenjie.dto.request.BorrowBookDTO;
import com.pwenjie.dto.response.BorrowRecordVO;
import com.pwenjie.entity.Book;
import com.pwenjie.entity.BorrowRecord;
import com.pwenjie.entity.User;
import com.pwenjie.mapper.BookMapper;
import com.pwenjie.mapper.BorrowRecordMapper;
import com.pwenjie.mapper.UserMapper;
import com.pwenjie.service.BorrowRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class BorrowRecordServiceImpl implements BorrowRecordService {

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public BorrowRecordVO borrowBook(BorrowBookDTO borrowBookDTO) {
        Long userId = borrowBookDTO.getUserId();
        Long bookId = borrowBookDTO.getBookId();

        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResponseCodeEnum.USER_NOT_EXIST);
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(ResponseCodeEnum.USER_DISABLED);
        }

        // 检查图书是否存在
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new BusinessException(ResponseCodeEnum.BOOK_NOT_EXIST);
        }
        if (book.getStatus() == null || book.getStatus() != BookStatusEnum.ONLINE.getCode()) {
            throw new BusinessException(ResponseCodeEnum.BOOK_STATUS_ERROR);
        }
        if (book.getStock() == null || book.getStock() <= 0) {
            throw new BusinessException(ResponseCodeEnum.BOOK_OUT_OF_STOCK);
        }

        // 检查用户是否有超期未还的书
        int overdueCount = borrowRecordMapper.countOverdueByUserId(userId);
        if (overdueCount > 0) {
            throw new BusinessException(ResponseCodeEnum.BORROW_OVERDUE);
        }

        // 检查借阅数量是否已达上限
        int currentBorrowCount = borrowRecordMapper.countBorrowingByUserId(userId);
        if (currentBorrowCount >= SystemConstants.MAX_BORROW_COUNT) {
            throw new BusinessException(ResponseCodeEnum.BORROW_OVER_LIMIT);
        }

        // 扣减图书库存
        int stockResult = bookMapper.decreaseStock(bookId);
        if (stockResult <= 0) {
            throw new BusinessException(ResponseCodeEnum.BOOK_OUT_OF_STOCK);
        }

        // 创建借阅记录
        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setBorrowTime(new Date());

        int borrowDays = borrowBookDTO.getBorrowDays() != null ? borrowBookDTO.getBorrowDays() : SystemConstants.DEFAULT_BORROW_DAYS;
        long dueTimeMillis = System.currentTimeMillis() + (long) borrowDays * 24 * 60 * 60 * 1000;
        record.setDueTime(new Date(dueTimeMillis));
        record.setStatus(BorrowStatusEnum.BORROWING.getCode());
        record.setCreateTime(new Date());

        int result = borrowRecordMapper.insert(record);
        if (result <= 0) {
            throw new BusinessException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.getCode(), "借阅失败");
        }

        clearBorrowCache(userId);

        BorrowRecordVO vo = convertToVO(record);
        vo.setUsername(user.getUsername());
        vo.setBookTitle(book.getTitle());
        return vo;
    }

    @Override
    @Transactional
    public BorrowRecordVO returnBook(Long recordId) {
        BorrowRecord record = borrowRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException(ResponseCodeEnum.BORROW_RECORD_NOT_EXIST);
        }

        if (record.getStatus() != null && record.getStatus() == BorrowStatusEnum.RETURNED.getCode()) {
            throw new BusinessException(ResponseCodeEnum.BAD_REQUEST.getCode(), "该图书已归还");
        }

        // 判断是否超期
        boolean isOverdue = record.getDueTime() != null && new Date().after(record.getDueTime());
        int newStatus = isOverdue ? BorrowStatusEnum.OVERDUE.getCode() : BorrowStatusEnum.RETURNED.getCode();

        Date now = new Date();
        String remark = isOverdue ? "超期归还" : "正常归还";

        borrowRecordMapper.updateReturnInfo(recordId, newStatus, now, remark);

        // 恢复图书库存
        bookMapper.increaseStock(record.getBookId());

        // 清除相关缓存
        clearBorrowCache(record.getUserId());
        redisTemplate.delete(CacheConstants.BOOK_PREFIX + record.getBookId());

        record.setStatus(newStatus);
        record.setReturnTime(now);
        record.setRemark(remark);

        BorrowRecordVO vo = convertToVO(record);
        vo.setOverdue(isOverdue);

        User user = userMapper.selectById(record.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
        }
        Book book = bookMapper.selectById(record.getBookId());
        if (book != null) {
            vo.setBookTitle(book.getTitle());
        }
        return vo;
    }

    @Override
    public BorrowRecordVO getBorrowRecordById(Long id) {
        BorrowRecord record = borrowRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(ResponseCodeEnum.BORROW_RECORD_NOT_EXIST);
        }
        return fillUserAndBookInfo(convertToVO(record));
    }

    @Override
    public List<BorrowRecordVO> getAllBorrowRecords() {
        List<BorrowRecord> records = borrowRecordMapper.selectAll();
        return records.stream()
                .map(r -> fillUserAndBookInfo(convertToVO(r)))
                .collect(Collectors.toList());
    }

    @Override
    public List<BorrowRecordVO> getBorrowRecordsByUserId(Long userId, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;

        int offset = (pageNum - 1) * pageSize;
        List<BorrowRecord> records = borrowRecordMapper.selectPageByUserId(userId, offset, pageSize);
        return records.stream()
                .map(r -> fillUserAndBookInfo(convertToVO(r)))
                .collect(Collectors.toList());
    }

    @Override
    public List<BorrowRecordVO> getBorrowRecordsByBookId(Long bookId) {
        List<BorrowRecord> records = borrowRecordMapper.selectByBookId(bookId);
        return records.stream()
                .map(r -> fillUserAndBookInfo(convertToVO(r)))
                .collect(Collectors.toList());
    }

    @Override
    public List<BorrowRecordVO> getBorrowingRecords() {
        List<BorrowRecord> records = borrowRecordMapper.selectByStatus(BorrowStatusEnum.BORROWING.getCode());
        return records.stream()
                .map(r -> fillUserAndBookInfo(convertToVO(r)))
                .collect(Collectors.toList());
    }

    @Override
    public List<BorrowRecordVO> getOverdueRecords() {
        List<BorrowRecord> records = borrowRecordMapper.selectByStatus(BorrowStatusEnum.OVERDUE.getCode());
        return records.stream()
                .map(r -> fillUserAndBookInfo(convertToVO(r)))
                .collect(Collectors.toList());
    }

    @Override
    public int getBorrowCount() {
        return borrowRecordMapper.count();
    }

    private BorrowRecordVO convertToVO(BorrowRecord record) {
        if (record == null) return null;
        BorrowRecordVO vo = new BorrowRecordVO();
        BeanUtils.copyProperties(record, vo);

        if (record.getDueTime() != null) {
            vo.setOverdue(record.getStatus() == null || record.getStatus() != BorrowStatusEnum.RETURNED.getCode()
                    ? new Date().after(record.getDueTime()) : false);
        }
        return vo;
    }

    private BorrowRecordVO fillUserAndBookInfo(BorrowRecordVO vo) {
        if (vo == null) return null;
        if (vo.getUserId() != null) {
            User user = userMapper.selectById(vo.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
            }
        }
        if (vo.getBookId() != null) {
            Book book = bookMapper.selectById(vo.getBookId());
            if (book != null) {
                vo.setBookTitle(book.getTitle());
            }
        }
        return vo;
    }

    private void clearBorrowCache(Long userId) {
        var keys = redisTemplate.keys(CacheConstants.BORROW_LIST_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
