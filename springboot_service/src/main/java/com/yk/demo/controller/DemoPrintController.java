package com.yk.demo.controller;

import com.yk.demo.model.DemoModel;
import com.yk.demo.model.PrintModel;
import com.yk.demo.service.Producer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
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

    @RequestMapping(method = RequestMethod.POST, value = "/print1", produces = "application/json")
    @ResponseBody
    public PrintModel print1(@RequestBody PrintModel printModel)
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
