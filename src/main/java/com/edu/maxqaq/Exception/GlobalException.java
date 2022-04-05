package com.edu.maxqaq.Exception;

import com.edu.maxqaq.vo.RespBeanEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: miaosha
 * @description:全局异常
 * @author: max-qaq
 * @create: 2022-04-01 13:01
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalException extends RuntimeException{
    private RespBeanEnum respBeanEnum;
}
