package com.yk.demo.controller;

import com.yk.demo.model.DemoModel;
import com.yk.demo.model.PrintModel;
import com.yk.demo.service.Producer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/print")
public class DemoPrintController implements InitializingBean
{
    @Autowired
    private Producer<PrintModel> producerPrintModel;

    @Autowired
    private Producer<DemoModel> producerDemoModel;

    /**
     * 为当前的Controller添加单独额校验
     */
    @InitBinder
    protected void initBinder(WebDataBinder binder)
    {
        binder.addValidators(new PrintModelValidator());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/print1", produces = "application/json")
    @ResponseBody
    public PrintModel print1(@Validated @RequestBody PrintModel printModel)
    {
        producerPrintModel.print(printModel);
        return printModel;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/print2", produces = "application/json")
    @ResponseBody
    public DemoModel print2(@RequestBody DemoModel demoModel)
    {
        producerDemoModel.print(demoModel);
        return demoModel;
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        System.out.println(producerPrintModel.hashCode());
        System.out.println(producerDemoModel.hashCode());
        System.out.println();
    }
}
