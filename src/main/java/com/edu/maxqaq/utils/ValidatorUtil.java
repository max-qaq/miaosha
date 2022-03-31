package com.edu.maxqaq.utils;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @program: miaosha
 * @description: 校验类
 * @author: max-qaq
 * @create: 2022-03-20 22:04
 **/

public class ValidatorUtil {
    //手机号
    private static final Pattern mobile_pattern = Pattern.compile("^1(3\\d|4[5-9]|5[0-35-9]|6[2567]|7[0-8]|8\\d|9[0-35-9])\\d{8}$");

    public static boolean isMobile(String mobile){
        //校验手机号
        if(StringUtils.isEmpty(mobile)) return false;
        Matcher matcher = mobile_pattern.matcher(mobile);
        return matcher.matches();
    }
}
