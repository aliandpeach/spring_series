package com.yk.demo.controller;

import com.yk.demo.model.DemoModel;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/demo")
public class DemoProductController {

    @RequestMapping(method = RequestMethod.GET, value = "/get1", consumes = "application/json", produces = "application/json")
    public DemoModel get(@RequestParam("id") Integer id) {
        DemoModel demoModel = new DemoModel();
        return demoModel;
    }

    @RequestMapping(method = RequestMethod.POST,value = "/get2", consumes = "application/x-www-form-urlencoded", produces = "application/json")
    public DemoModel get2(MultiValueMap<String, String> params) {
        DemoModel demoModel = new DemoModel();
        return demoModel;
    }
    @RequestMapping(method = RequestMethod.POST,value = "/get3", consumes = "application/x-www-form-urlencoded", produces = "application/json")
    public DemoModel get3(@ModelAttribute("params") Map<String, String> params) {
        DemoModel demoModel = new DemoModel();
        return demoModel;
    }
    @RequestMapping(method = RequestMethod.POST,value = "/get4", consumes = "application/x-www-form-urlencoded", produces = "application/json")
    public DemoModel get4(@RequestParam Map<String, String> params) {
        DemoModel demoModel = new DemoModel();
        return demoModel;
    }

    @RequestMapping(method = RequestMethod.POST,value = "/get5", consumes = "application/json", produces = "application/json")
    public DemoModel get5(@RequestBody Map<String, Object> params) {
        DemoModel demoModel = new DemoModel();
        return demoModel;
    }

}
