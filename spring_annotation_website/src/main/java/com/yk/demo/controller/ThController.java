package com.yk.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * ThController
 */
@Controller
@RequestMapping("/th")
public class ThController
{
    @RequestMapping(value = "/demo", method = RequestMethod.GET)
    public String info()
    {
        return "demo/demo";
    }
}