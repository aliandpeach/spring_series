package com.yk.demo;

import com.yk.base.config.BlockchainProperties;
import com.yk.bitcoin.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import static com.yk.bitcoin.KeyCache.LOCK;

@Controller
@RequestMapping("/")
public class IndexController
{
    @Autowired
    private Cache cache;
    
    @Autowired
    private BlockchainProperties blockchainProperties;
    
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView welcome()
    {
        ModelAndView model = new ModelAndView("index");
    
        boolean run = cache.isRun();
        model.addObject("run", run);
        synchronized (LOCK)
        {
            model.addObject("minKey", cache.getMin().toString(16).toUpperCase());
            model.addObject("maxKey", cache.getMax().toString(16).toUpperCase());
        }
        String _status = !blockchainProperties.isExecute() ? "un-execute" : run ? "started" : "stopped";
        model.addObject("status", _status);
        return model;
    }
}
