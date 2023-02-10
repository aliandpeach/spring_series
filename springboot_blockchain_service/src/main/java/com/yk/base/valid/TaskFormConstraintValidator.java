package com.yk.base.valid;

import com.yk.bitcoin.model.TaskForm;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigInteger;

public class TaskFormConstraintValidator implements ConstraintValidator<TaskFormValid, TaskForm>
{
    @Override
    public void initialize(TaskFormValid constraintAnnotation)
    {
    }

    @Override
    public boolean isValid(TaskForm taskForm, ConstraintValidatorContext constraintValidatorContext)
    {
        if (taskForm.getType() == 1)
        {
            return true;
        }
        return taskForm.getType() == 0 && (StringUtils.isNotBlank(taskForm.getMin()) && StringUtils.isNotBlank(taskForm.getMin()));
    }
}
