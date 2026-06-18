package com.pwenjie.service;

import com.pwenjie.common.enums.BorrowStatusEnum;
import com.pwenjie.mapper.BorrowRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class OverdueScheduler {

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void scanOverdueBorrows() {
        log.debug("开始扫描超期借阅...");
        // 超期自动从图书借阅中标记为超期状态
        // 直接数据库更新效率更高，通过Mapper执行批量更新
        int count = borrowRecordMapper.updateOverdueStatus(BorrowStatusEnum.OVERDUE.getCode(), new Date());
        if (count > 0) {
            log.info("超期借阅扫描完成，更新 {} 条记录", count);
        }
    }
}
