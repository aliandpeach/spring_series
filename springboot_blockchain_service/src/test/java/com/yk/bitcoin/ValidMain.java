package com.yk.bitcoin;

import com.yk.base.valid.GroupConstant;
import com.yk.bitcoin.model.TaskForm;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import java.util.Set;

public class ValidMain
{
    @Test
    public void test1()
    {
        TaskForm taskForm = new TaskForm();
        taskForm.setMin("123");
        taskForm.setMax("456");
        taskForm.setType(3);
        Set<ConstraintViolation<TaskForm>> result = Validation.buildDefaultValidatorFactory().getValidator().validate(taskForm, GroupConstant.SequentialCombination1.class);

        result.stream().map(v -> v.getPropertyPath() + " " + v.getMessage() + ": " + v.getInvalidValue()).forEach(System.out::println);
    }

    @Test
    public void test2()
    {
        TaskForm taskForm = new TaskForm();
        Set<ConstraintViolation<TaskForm>> result = Validation.buildDefaultValidatorFactory().getValidator().validate(taskForm, GroupConstant.SequentialCombination2.class);

        result.stream().map(v -> v.getPropertyPath() + " " + v.getMessage() + ": " + v.getInvalidValue()).forEach(System.out::println);
    }
}
