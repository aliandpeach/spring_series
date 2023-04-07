package com.yk.base.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TaskTypeConstraintValidator implements ConstraintValidator<TaskTypeValid, Integer>
{
    @Override
    public void initialize(TaskTypeValid constraintAnnotation)
    {
    }

    @Override
    public boolean isValid(Integer type, ConstraintValidatorContext constraintValidatorContext)
    {
        /*return KeyCache.TASK_CONTEXT.containsKey(new Task(AbstractKeyGenerator.getKeyGeneratorName(type))) &&
                KeyCache.TASK_CONTEXT.get(new Task(AbstractKeyGenerator.getKeyGeneratorName(type))) != null;*/
        return true;
    }
}
