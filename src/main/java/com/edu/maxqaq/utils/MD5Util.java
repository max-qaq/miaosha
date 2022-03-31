package com.edu.maxqaq.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.context.annotation.Configuration;
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

    private static final String salt = "1a2b3c4d";

    //前端密码第一次加密
    public static String inputPassToFormPass(String inputPass){
        String str = "" + salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
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

    public static void main(String[] args) {
        String str = "c3f53af99f9214cd7a252899065cb0fc";
        String passwd = "4587889";
        String jiami1 = inputPassToFormPass(passwd);
        String jiami2 = FormPasstoDBPass(jiami1,"1a2b3c4d");
        System.out.println(jiami1);
        System.out.println(jiami2);
    }
}
