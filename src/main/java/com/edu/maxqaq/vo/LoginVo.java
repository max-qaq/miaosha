package com.edu.maxqaq.vo;

import com.edu.maxqaq.utils.validator.IsMobile;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @program: miaosha
 * @description: 登录参数
 * @author: max-qaq
 * @create: 2022-03-20 21:43
 **/
@Data
public class LoginVo {
    @NotNull
    @IsMobile
    private String mobile;

    @NotNull
    @Length(min = 3)
    private String password;
}
