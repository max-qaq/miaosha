package com.edu.maxqaq.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: miaosha
 * @description: 公共返回对象
 * @author: max-qaq
 * @create: 2022-03-20 21:24
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespBean {
    private long code;
    private String message;
    private Object obj;

    public static RespBean success(){
        return new RespBean(RespBeanEnum.SUCCESS.getCode(), RespBeanEnum.SUCCESS.getMessage(), null);
    }

    public static RespBean success(Object obj){
        return new RespBean(RespBeanEnum.SUCCESS.getCode(), RespBeanEnum.SUCCESS.getMessage(), obj);
    }

    public static RespBean error(RespBeanEnum respBeanEnum){
        return new RespBean(respBeanEnum.ERROR.getCode(), respBeanEnum.ERROR.getMessage(), null);
    }

    public static RespBean error(RespBeanEnum respBeanEnum , Object obj){
        return new RespBean(respBeanEnum.ERROR.getCode(), respBeanEnum.ERROR.getMessage(), obj);
    }
}
