package com.yk.demo;

import com.yk.bitcoin.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/block/chain")
public class BlockchainController
{
    @Autowired
    private Cache cache;

    @RequestMapping(value = "/welcome", method = RequestMethod.GET)
    public ModelAndView welcome()
    {
        ModelAndView model = new ModelAndView("index");
        return model;
    }

    @RequestMapping(value = "/{status}", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Map<String, String> opt(@PathVariable("status") String status)
    {
        if (null != status && status.equalsIgnoreCase("start"))
        {
            synchronized (this)
            {
                cache.run = true;
            }
        }
        else
        {
            synchronized (this)
            {
                cache.run = false;
            }
        }
        return new HashMap<>(Collections.singletonMap("status", "OK"));
    }
}
