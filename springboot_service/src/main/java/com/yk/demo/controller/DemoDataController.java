package com.yk.demo.controller;

import com.yk.base.exception.CustomException;
import com.yk.demo.DemoDAO;
import com.yk.demo.model.DemoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/data")
public class DemoDataController {

    @Autowired
    private DemoDAO demoDAO;

    @RequestMapping(method = RequestMethod.POST, value = "/query", produces = "application/json")
    @ResponseBody
    public List<Map<String, Object>> query()
    {
        List<Map<String, Object>> r = demoDAO.query();
        return r;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/query/demo", produces = "application/json")
    @ResponseBody
    public List<Map<String, Object>> query(@Validated DemoModel demoModel, BindingResult bindingResult)
    {
//        Errors errors = new BeanPropertyBindingResult(demoModel, "demoModel");
//        new DemoModelValidator().validate(demoModel, errors);

        if (!bindingResult.hasErrors())
        {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            fieldErrors.forEach(e ->
            {
                System.out.println(e.getField() + e.getCode());
            });
            throw new CustomException("参数输入错误");
        }

        List<Map<String, Object>> r = demoDAO.query();
        return r;
    }
}
