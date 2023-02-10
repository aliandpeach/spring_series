package com.yk.bitcoin;

import com.yk.base.valid.GroupConstant;
import com.yk.bitcoin.model.TaskForm;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import java.util.Set;

public class ValidMain
{
    public static void main(String[] args)
    {
        TaskForm taskForm = new TaskForm();
        taskForm.setMin("123");
        taskForm.setMax("456");
        Set<ConstraintViolation<TaskForm>> result = Validation.buildDefaultValidatorFactory().getValidator().validate(taskForm, GroupConstant.SequentialCombination2.class);

        result.stream().map(v -> v.getPropertyPath() + " " + v.getMessage() + ": " + v.getInvalidValue()).forEach(System.out::println);
    }
}
