package com.yk.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * PageController
 */
@Controller
@RequestMapping("/page")
public class PageController
{
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public ModelAndView info()
    {
        return new ModelAndView("page/info");
    }

    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public ModelAndView error()
    {
        throw new RuntimeException("1234");
    }
}