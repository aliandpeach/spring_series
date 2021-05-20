package com.yk.demo;

import com.yk.demo.dao.DemoDAO;
import com.yk.demo.dao.IOtherDAO;
import com.yk.demo.model.BlockchainModel;
import com.yk.demo.model.DemoModel;
import com.yk.demo.model.GroupInterface;
import com.yk.demo.service.DemoService;
import com.yk.performance.FileInfos;
import com.yk.util.ValidationTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DemoController
 */
@RestController
@RequestMapping("/demo")
public class DemoController
{
    @Autowired
    private DemoDAO demoDAO;
    
    @Autowired(required = false)
    private IOtherDAO otherDAO;
    
    @Autowired
    private DemoService demoService;
    
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
    
    /**
     * 注解@Validated 配合@NotNull 等注解 进行验证。
     * <p>
     * 注解@Validated 配合@NotNull(groups="") 等注解 可以对同一个类中的不同属性进行分组验证
     */
    @RequestMapping(value = "/validated/detail", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> detail(@RequestBody @Validated({GroupInterface.ITheAll.class}) BlockchainModel blockchainModel)
    {
        Map<String, String> result = new HashMap<>();
        return result;
    }
    
    /**
     * 注解@Valid 配合@NotNull 等注解 进行验证。
     */
    @RequestMapping(value = "/valid/detail", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> detailApi(@RequestBody @Valid DemoModel demoModel) throws InterruptedException
    {
        String id = demoModel.getId() + "_" + UUID.randomUUID().toString().replace("-", "");
        Map<String, String> result = new HashMap<>();
        demoService.send(id + "", result1 ->
        {
            result.putAll(result1);
            synchronized (id)
            {
                id.notifyAll();
            }
        });
        synchronized (id)
        {
            id.wait(30 * 1000);
        }
        return result;
    }

    /**
     * 框架无法校验 List<DemoModel> 集合中的对象  只能通过 ValidationTool.validate(demoModelList); 进行校验
     */
    @RequestMapping(value = "/valid/list", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> detailApi(@RequestBody List<DemoModel> demoModelList)
    {
        ValidationTool.validate(demoModelList);
        Map<String, String> result = new HashMap<>();
        return result;
    }

    /**
     * 注解 @Validated 配合 FileInfos内部 属性 @Valid List<FileInfoParam>  可以校验 List<FileInfoParam>集合内部的对象的属性
     */
    @PostMapping(value = "/upload/multiple/xml", consumes = "application/xml", produces = "application/xml")
    @ResponseBody
    public FileInfos uploadXml(@RequestBody @Validated FileInfos fileInfos)
    {
        return fileInfos;
    }
}
