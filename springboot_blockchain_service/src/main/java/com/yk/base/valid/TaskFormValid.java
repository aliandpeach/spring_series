package com.yk.base.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {TaskFormConstraintValidator.class})
public @interface TaskFormValid
{
    String message() default "类型为0则min和max不能为空";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}


