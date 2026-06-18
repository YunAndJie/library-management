package com.pwenjie.common.utils;

import com.pwenjie.common.constant.UserConstants;
import io.micrometer.common.util.StringUtils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

public class Md5Util {

    public static String md5(String input){
        if(StringUtils.isEmpty(input)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(input.getBytes(StandardCharsets.UTF_8));
    }


    public static String md5WithSalt(String input){
        if(StringUtils.isEmpty(input)){
            return null;
        }
        String saltInput = UserConstants.PASSWORD_SALT + input;
        return md5(saltInput);
    }


    public static boolean verify(String input, String encrypted){
        if(StringUtils.isEmpty(input) || StringUtils.isEmpty(encrypted)){
            return false;
        }
        String encryptedInput = md5WithSalt(input);
        return encrypted.equals(encryptedInput);
    }
}
