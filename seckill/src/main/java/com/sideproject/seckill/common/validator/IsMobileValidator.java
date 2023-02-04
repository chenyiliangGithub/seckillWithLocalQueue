package com.sideproject.seckill.common.validator;

import com.sideproject.seckill.utils.ValidatorUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;


/**
 * 校验手机号码的实现类
 * 手机号码类型 Long
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile, Long> {
    private boolean required = true;

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        String valueStr = String.valueOf(value);
        if (required){
            return ValidatorUtil.isMobile(valueStr);
        }
        return true;
    }
}
