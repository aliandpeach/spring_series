package com.yk.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * PageController
 */
@Controller
@RequestMapping("/view")
public class PageController
{
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public String info()
    {
        return "page/demo/info";
    }
}