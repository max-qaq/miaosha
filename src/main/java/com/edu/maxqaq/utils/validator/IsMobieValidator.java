package com.edu.maxqaq.utils.validator;

import com.edu.maxqaq.utils.ValidatorUtil;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @program: miaosha
 * @description: 手机类校验规则
 * @author: max-qaq
 * @create: 2022-03-31 21:51
 **/

public class IsMobieValidator implements ConstraintValidator<IsMobile , String> {

    private boolean required  = false;

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        //初始化
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (required){
            return ValidatorUtil.isMobile(s);
        }else{
            if(StringUtils.isEmpty(s)){
                return true;
            }else{
                ValidatorUtil.isMobile(s);
            }
        }
        return false;
    }
}
