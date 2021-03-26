package com.yk.demo;

import com.yk.bitcoin.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class IndexController
{
    @Autowired
    private Cache cache;
    
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView welcome()
    {
        ModelAndView model = new ModelAndView("index");
        
        boolean run = cache.isRun();
        model.addObject("run", run);
        return model;
    }
}
