package com.yk.demo.controller;

import com.yk.demo.model.PrintModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * PrintModel对象的校验器, 使用@InitBinder注册到 DemoPrintController
 */
public class PrintModelValidator implements Validator
{
    @Override
    public boolean supports(Class<?> clazz)
    {
        return PrintModel.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors)
    {
        PrintModel printModel = (PrintModel) target;
        // rejectIfEmpty内部可以获取到field的具体值
        ValidationUtils.rejectIfEmpty(errors, "name", "name不能为空");

        // 手动判断
        if (StringUtils.isBlank(printModel.getMessage()))
        {
            errors.rejectValue("message", "message is blank");
        }
    }
}