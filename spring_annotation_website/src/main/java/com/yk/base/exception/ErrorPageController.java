package com.yk.base.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Locale;

@RequestMapping("/error")
public class ErrorPageController
{
    @RequestMapping("")
    public ModelAndView errorPage(Locale locale, Model model)
    {
        return new ModelAndView("error/400");
    }
}