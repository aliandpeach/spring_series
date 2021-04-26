package com.yk.demo;

import com.yk.demo.dao.DemoDAO;
import com.yk.demo.dao.IOtherDAO;
import com.yk.demo.model.DemoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * DemoController
 */
@RestController
@RequestMapping("/demo")
public class DemoController
{
    @Autowired
    private DemoDAO demoDAO;
    
    @Autowired
    private IOtherDAO otherDAO;
    
    @GetMapping("/query")
    @ResponseBody
    public List<DemoModel> queryByName(@RequestParam String name)
    {
        List<DemoModel> list2 = demoDAO.queryByName2(name);
        list2 = otherDAO.queryBy(name);
        return demoDAO.queryByName(name);
    }
    
    @GetMapping("/query/redis")
    @ResponseBody
    public Map<Object, Object> queryRedis(@RequestParam String name)
    {
        return demoDAO.queryRedis(name);
    }
}
