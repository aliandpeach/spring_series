package com.yk.demo.controller;

import com.yk.demo.DemoDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
    public List<Map<String, Object>> query() {
        List<Map<String, Object>> r = demoDAO.query();
        return r;
    }
}
