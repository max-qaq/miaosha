package com.edu.maxqaq.utils;

import java.util.UUID;

/**
 * @program: miaosha
 * @description:UUID工具类
 * @author: max-qaq
 * @create: 2022-04-02 21:17
 **/

public class UUIDUtil {
    public static String uuid(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
