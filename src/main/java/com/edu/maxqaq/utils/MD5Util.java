package com.edu.maxqaq.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

/**
 * @program: miaosha
 * @description:
 * @author: max-qaq
 * @create: 2022-03-18 10:53
 **/
@Component
public class MD5Util {
    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    private static final String salt = "1d2c3b4a";

    //前端密码第一次加密
    public static String inputPassToFormPass(String inputPass){
        String str = salt.charAt(0) + salt.charAt(5) + inputPass + salt.charAt(4);
        return md5(str);
    }

    //后端第二次加密
    public static String FormPasstoDBPass(String formPass,String salt){
        String str = salt.charAt(0) + salt.charAt(5) + formPass;
        return md5(str);
    }

    public static String inputPassToDBpass(String inputPass, String salt){
        String formPass = inputPassToFormPass(inputPass);
        String dbPass = FormPasstoDBPass(formPass, salt);
        return dbPass;
    }

}
