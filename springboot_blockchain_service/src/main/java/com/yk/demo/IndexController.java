package com.yk.demo;

import com.yk.base.config.BlockchainProperties;
import com.yk.bitcoin.Context;
import com.yk.bitcoin.KeyCache;
import com.yk.bitcoin.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequestMapping("/")
public class IndexController
{
    @Autowired
    private BlockchainProperties blockchainProperties;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView welcome()
    {
        ModelAndView model = new ModelAndView("index");

        Context context = null;
        for (Map.Entry<Task, Context> entry : KeyCache.TASK_CONTEXT.entrySet())
        {
            context = entry.getValue();
            if (context.getTask().getState() == 1)
            {
                break;
            }
        }

        if (null == context || context.getTask() == null)
        {
            model.addObject("run", false);
            model.addObject("status", "stopped");
//            model.addObject("minKey", blockchainProperties.getMinKey());
//            model.addObject("maxKey", blockchainProperties.getMaxKey());
            return model;
        }

        try
        {
            context.getLock().lock();
            model.addObject("minKey", context.getTask().getType() == 0 ? context.getTask().getMin().toString(16).toUpperCase() : "");
            model.addObject("maxKey", context.getTask().getType() == 0 ? context.getTask().getMax().toString(16).toUpperCase() : "");
            model.addObject("run", context.queryTaskStatus() == 1);
            String _status = context.queryTaskStatus() == 1 ? "started" : "stopped";
            model.addObject("status", _status);
            model.addObject("type", context.getTask().getType());
        }
        finally
        {
            context.getLock().unlock();
        }
        return model;
    }

    @RequestMapping(value = "/page/upload", method = RequestMethod.GET)
    public ModelAndView upload()
    {
        ModelAndView model = new ModelAndView("upload");

        return model;
    }
}
