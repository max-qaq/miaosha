package com.edu.maxqaq.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @program: miaosha
 * @description: 公共返回对象枚举
 * @author: max-qaq
 * @create: 2022-03-20 21:25
 **/
@Getter
@ToString
@AllArgsConstructor
public enum RespBeanEnum {
    //通用
    SUCCESS(200,"成功"),
    ERROR(500,"服务端异常"),
    //登录模块
    LOGIN_ERROR(500210,"用户名或密码错误"),
    MOBILE_ERROR(500220,"手机号格式错误"),
    BIND_ERROR(500230,"参数校验异常"),
    MOBILE_NOT_EXIST(500200,"手机号码不存在"),
    PASSWORD_UPDATE_FAIL(500000,"数据更新错误"),
    //秒杀模块
    EMPTY_STOCK(500330,"库存不足"),
    REPEATE_ERROR(500340,"该商品限购一件")
    ;
    private final Integer code;
    private final String message;
}
