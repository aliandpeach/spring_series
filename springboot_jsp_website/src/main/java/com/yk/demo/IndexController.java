package com.yk.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("")
public class IndexController
{
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("/")
    public ModelAndView welcome()
    {
        return new ModelAndView("view");
    }
}
