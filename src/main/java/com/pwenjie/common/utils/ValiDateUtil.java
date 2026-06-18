package com.pwenjie.common.utils;

import com.pwenjie.common.constant.UserConstants;
import io.micrometer.common.util.StringUtils;

import java.util.regex.Pattern;

public class ValiDateUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(UserConstants.EMAIL_PATTERN);
    private static final Pattern PHONE_PATTERN = Pattern.compile(UserConstants.PHONE_PATTERN);
    private static final Pattern USERNAME_PATTERN = Pattern.compile(UserConstants.USERNAME_PATTERN);
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(UserConstants.PASSWORD_PATTERN);

    public static boolean isEmail(String email){
        if(StringUtils.isEmpty(email)){
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isPhone(String phone){
        if(StringUtils.isEmpty(phone)){
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isUsername(String userName){
        if(StringUtils.isEmpty(userName)){
            return false;
        }
        return USERNAME_PATTERN.matcher(userName).matches();
    }

    public static boolean isPassword(String password){
        if(StringUtils.isEmpty(password)){
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean isISBN(String isbn){
        if(StringUtils.isEmpty(isbn)){
            return false;
        }
        String cleanIsbn = isbn.replaceAll("-", "");
        return  cleanIsbn.length() == 10 || cleanIsbn.length() == 13;
    }
}
