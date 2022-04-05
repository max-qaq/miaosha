package com.edu.maxqaq.utils.validator;

/**
 * @program: miaosha
 * @description: 验证手机号
 * @author: max-qaq
 * @create: 2022-03-31 21:47
 **/

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        //校验规则
        validatedBy = { IsMobieValidator.class }
)
public @interface IsMobile {

    boolean required() default true;//必填

    String message() default "手机号码格式错误";//报错消息

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
