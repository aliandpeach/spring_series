package com.yk.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LoginController {
    private Logger logger = LoggerFactory.getLogger("demo");

    @Autowired
    private HttpServletRequest request;

    public ModelAndView login() {
        return new ModelAndView("login");

    }

}
