package com.pwenjie.service.impl;

import com.pwenjie.mapper.TestMapper;
import com.pwenjie.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private TestMapper testMapper;

    @Override
    public Integer testDbConnection() {
        return testMapper.testConnection();
    }
}