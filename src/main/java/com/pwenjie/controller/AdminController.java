package com.pwenjie.controller;

import com.pwenjie.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pwenjie.service.BookService;
import com.pwenjie.service.BorrowRecordService;
import com.pwenjie.service.CategroyService;
import com.pwenjie.service.UserService;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @Autowired
    private CategroyService categroyService;

    @Autowired
    private BorrowRecordService borrowRecordService;

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("userCount", userService.getUserCount());
        data.put("bookCount", bookService.getBookCount());
        data.put("categoryCount", categroyService.getCategoryCount());
        data.put("borrowCount", borrowRecordService.getBorrowCount());
        return Result.success(data);
    }
}
