package com.yk.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * PageController
 */
@Controller
@RequestMapping("/")
public class IndexController
{
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView info()
    {
        return new ModelAndView("page/info");
    }
}