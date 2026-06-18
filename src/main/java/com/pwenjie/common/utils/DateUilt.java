//TODO日期时间工具

package com.pwenjie.common.utils;

import com.pwenjie.common.constant.SystemConstants;
import io.micrometer.common.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUilt {
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_FORMAT = "HH:mm:ss";


    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

    public static String getNowDate(){
        return LocalDate.now().format(dateFormatter);
    }

    public static String getNowDateTime(){
        return LocalDateTime.now().format(dateTimeFormatter);
    }


    //日期转字符
    public static String format(Date date, String pattern){
        if(date == null){
            return null;
        }

        return new SimpleDateFormat(pattern).format(date);
    }

    //日期转字符（默认格式）
    public static String format(Date date) {
        return format(date, DATE_TIME_FORMAT);
    }

    //字符转日期
    public static Date parse(String dateStr, String pattern){
        if(StringUtils.isEmpty(dateStr)){
            return null;
        }

        try {
            return new SimpleDateFormat(pattern).parse(dateStr);
        }catch (Exception e){
            return null;
        }
    }

    //字符转日期（默认）
    public static Date parse(String dateStr){
        return parse(dateStr, DATE_TIME_FORMAT);
    }

    //计算借阅截至日期
    public static Date calculateDueDate(int days) {
        long time = System.currentTimeMillis() + (long) days * 24 * 60 * 60 * 1000;
        return new Date(time);
    }

    public static Date calculateDueDate() {
        return calculateDueDate(SystemConstants.DEFAULT_BORROW_DAYS);
    }

    public static boolean isOverdue(Date dueDate){
        if(dueDate == null){
            return false;
        }
        return new Date().after(dueDate);
    }

    public static String getTimestemp(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
    }

}
