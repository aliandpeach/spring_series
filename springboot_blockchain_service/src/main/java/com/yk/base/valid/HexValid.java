package com.yk.base.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {HexConstraintValidator.class})
public @interface HexValid
{
    String message() default "非法的16进制字符串";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}


