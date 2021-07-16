package com.yk.index.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * DownloadController
 *
 * @author yangk
 * @version 1.0
 * @since 2021/06/16 12:07:14
 */
@Controller
@RequestMapping("/")
public class IndexController
{
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ModelAndView index()
    {
        return new ModelAndView("index");
    }
}
