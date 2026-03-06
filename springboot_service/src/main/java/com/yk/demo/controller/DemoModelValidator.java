package com.yk.demo.controller;

import com.yk.demo.model.DemoModel;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * DemoModel对象的校验器, 是否在DemoDataController中生效需要验证
 * (直接使用@Validated可能不会生效, 因为Validator接口是spring的, @Validated 触发的是 Hibernate Validator 的注解校验（如 @NotNull）)
 */
public class DemoModelValidator implements Validator
{
    @Override
    public boolean supports(Class<?> clazz)
    {
        return DemoModel.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors)
    {
        DemoModel dto = (DemoModel) target;
        String option = dto.getName();
        if (option != null && !("A".equals(option) || "B".equals(option) || "C".equals(option)))
        {
            errors.rejectValue("option", "invalid.option", "Must be A, B, or C");
        }
    }
}