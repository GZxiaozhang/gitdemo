package com.example.myseckill_demo.vo;


//手机号码校验规则

import com.example.myseckill_demo.utils.ValidatorUtil;
import com.example.myseckill_demo.validator.IsMobile;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class IsMobileValidator implements ConstraintValidator<IsMobile,String> {

    private boolean required =false;

    @Override
    public void initialize(IsMobile constraintAnnotation) {
         required= constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(required){
        return ValidatorUtil.isMobile(value);
        }else{
            if(StringUtils.isEmpty(value)){
            }else{
                return ValidatorUtil.isMobile(value);
            }
        }

        return false;
    }
}
