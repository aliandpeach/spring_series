package com.yk.demo;

import com.yk.demo.dao.DemoDAO;
import com.yk.demo.dao.IOtherDAO;
import com.yk.demo.model.BlockchainModel;
import com.yk.demo.model.DemoModel;
import com.yk.demo.model.GroupInterface;
import com.yk.demo.service.DemoService;
import com.yk.performance.ArrayModel;
import com.yk.performance.FileInfoParam;
import com.yk.performance.FileInfos;
import com.yk.performance.ListModel;
import com.yk.util.ValidationTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collections;
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

    /**
     * ajax默认是以application/x-www-form-urlencoded方式提交。也就是常见的表单提交方式。
     * ajax中 data可以写成以下两种方式, contentType可以写成application/x-www-form-urlencoded或者application/json
     * ( contentType为application/x-www-form-urlencoded, ajax会自动将json对象转化为&连接的key=value格式的数据 )

        $.ajax({
           type: "get",
           contentType: "application/x-www-form-urlencoded",
           url: "/demo/fileInfoParam",
           data: 'id=1&name=2',
            async: true,
            success: function (result) {
                // debugger
            }
        });

        $.ajax({
             type: "get",
             contentType: "application/json",
             url: "/demo/fileInfoParam",
             data: {"id": "1", "name": "2"},
              async: true,
              success: function (result) {
                  // debugger
              }
        });
     */
    @GetMapping("/fileInfoParam")
    @ResponseBody
    public FileInfoParam fileInfoParam(@Validated FileInfoParam fileInfoParam)
    {
        return fileInfoParam;
    }

    /**
     * ajax默认是以application/x-www-form-urlencoded方式提交。也就是常见的表单提交方式。
     * ajax中 data可以写成以下两种方式, contentType可以写成application/x-www-form-urlencoded或者application/json
     * ( contentType为application/x-www-form-urlencoded, ajax会自动将json对象转化为&连接的key=value格式的数据 )

        $.ajax({
           type: "get",
           contentType: "application/x-www-form-urlencoded",
           url: "/demo/map",
           data: 'id=1&name=2',
            async: true,
            success: function (result) {
                // debugger
            }
        });

         $.ajax({
             type: "get",
             contentType: "application/x-www-form-urlencoded",
             url: "/demo/map",
             data: {"id": "1", "name": "2"},
              async: true,
              success: function (result) {
                  console.log(result);
              }
         });
     */
    @GetMapping("/map")
    @ResponseBody
    public Map<String, String> fileInfoParam(@RequestParam Map<String, String> map)
    {
        return map;
    }

    /**
     *
     */
    @GetMapping("/query")
    @ResponseBody
    public List<DemoModel> queryByName(@RequestParam String name)
    {
        List<DemoModel> list2 = demoDAO.queryByName2(name);
        list2 = otherDAO.queryBy(name);
        Map<String, Object> variables = otherDAO.showVariable("max_allowed_packet");
        List<Map<String, Object>> result = otherDAO.query(1, "name1");
        return demoDAO.queryByName(name);
    }

    /**
     *  application/x-www-form-urlencoded，表单格式数据，实际上就是&连接的key=value格式的数据
     *  multipart/form-data，一般用作文件上传，也就是说当文件上传和普通请求参数混合一起的时候使用这种格式提交表单
     *  application/json，发送的数据格式为json字符串
     *
     *  1. &连接的key=value，适用于GET和POST请求，此时contentType必须为application/x-www-form-urlencoded，后端不能使用@RequestBody注解
     *  2. json对象，适用于GET和POST请求，且此时contentType必须为application/x-www-form-urlencoded，
     *     ajax会自动将json对象转化为&连接的key=value格式的数据，GET请求就拼接在url后面，POST请求就放入post请求体中，后端不能使用@RequestBody注解
     *  3. json字符串，只适用于POST请求，且此时contentType必须为application/json，后端必须使用@RequestBody注解
     *
     *   RequestParam对于jsonData的json数据只能用String字符串来接收
     *
     *  ajax默认是以application/x-www-form-urlencoded方式提交。也就是常见的表单提交方式。
     *  如果使用ajax指定application/json方式，data参数则是字符串类型的。使用JSON.stringify()转换一下

          $.ajax({
             type: "post",
             contentType: "application/json",
             url: "/demo/fileInfoParam/2",
             data: JSON.stringify({"id": "1", "name": "2"}),
              async: true,
              success: function (result) {
                 // debugger
              }
          });
     */
    @PostMapping("/fileInfoParam/2")
    @ResponseBody
    public FileInfoParam fileInfoParam2(@Validated @RequestBody FileInfoParam fileInfoParam)
    {
        return fileInfoParam;
    }

    /**
     *  ( contentType为application/x-www-form-urlencoded, ajax会自动将json对象转化为&连接的key=value格式的数据 )
     *  后端不能使用@RequestBody注解
     *
     *   RequestMethod.POST 参数为 @RequestParam， 请求体中需要传入的格式为 key1=value1&key2=value2
     *   或者使用form-data ( 不指定consumes)
     *
     *   这里的参数不同于 @RequestParam Map<String, Object> param 对象前面不能加 @RequestParam
     *
           $.ajax({
               type: "post",
               dataType: "json",    // The type of data that you're expecting back from the server
               contentType: "application/x-www-form-urlencoded",
               url: "/demo/fileInfoParam/3",
               data: {"id": "1", "name": "2"},
                async: true,
                success: function (result) {
                   // debugger
                }
           });

           $.ajax({
               type: "post",
               dataType: "json",    // The type of data that you're expecting back from the server
               contentType: "application/x-www-form-urlencoded",
               url: "/demo/fileInfoParam/3",
               data: 'id=7&name=8',
               async: true,
               success: function (result) {
                  // debugger
               }
           });
     */
    @PostMapping(value = "/fileInfoParam/3")
    @ResponseBody
    public FileInfoParam fileInfoParam3(@Validated FileInfoParam fileInfoParam)
    {
        return fileInfoParam;
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
    @PostMapping(value = "/request/body/xml", consumes = "application/xml", produces = "application/xml")
    @ResponseBody
    public FileInfos xml(@RequestBody @Validated FileInfos fileInfos)
    {
        return fileInfos;
    }

    @GetMapping(value = "/request/body/xml/get", produces = "application/xml")
    @ResponseBody
    public FileInfos get()
    {
        FileInfos fileInfos = new FileInfos();
        FileInfoParam fileInfoParam = new FileInfoParam();
        fileInfoParam.setId("1000");
        fileInfoParam.setName("1000");
        fileInfos.setFileInfoParamList(Collections.singletonList(fileInfoParam));
        return fileInfos;
    }

    /**
     *
     */
    @PostMapping(value = "/request/body/array")
    @ResponseBody
    public ArrayModel array(@RequestBody @Validated ArrayModel arrayModel)
    {
        return arrayModel;
    }

    /**
     *
     */
    @PostMapping(value = "/request/body/list")
    @ResponseBody
    public ListModel list(@RequestBody @Validated ListModel listModel)
    {
        return listModel;
    }

    @PostMapping(value = "/request/body/list/string")
    @ResponseBody
    public List<String> listString(@RequestBody @Validated @NotEmpty(message = "empty") List<@Valid String> ids) // not use
    {
        return ids;
    }

    /**
     * form-data或者www-form-urlencoded格式的body
     */
    @PostMapping(value = "/request/body/id")
    @ResponseBody
    public String id(String dataId)
    {
        return dataId;
    }

    /**
     * 接收参数格式:
     *
     * POST /demo/request/part/object HTTP/1.1
     * User-Agent: PostmanRuntime/7.26.8
     * Accept:
     * Postman-Token: 064a8c62-3dee-4143-b4b2-15ff3113458c
     * Host: 192.168.31.158:21111
     * Accept-Encoding: gzip, deflate
     * Connection: close
     * Content-Type: multipart/form-data; boundary=--------------------------777582379408141727964937
     * Content-Length: 219
     *
     * ----------------------------777582379408141727964937
     * Content-Disposition: form-data; name="demoModel"
     * Content-Type: application/json
     *
     * {"id": "1", "name":"2"}
     * ----------------------------777582379408141727964937--
     */
    @PostMapping(value = "/request/part/object")
    @ResponseBody
    public DemoModel part(@RequestPart DemoModel demoModel)
    {
        return demoModel;
    }


    /**
     * 接收两种参数格式:
     *
     * ### 1.
     * POST /demo/request/object HTTP/1.1
     * User-Agent: PostmanRuntime/7.26.8
     * Accept:
     * Postman-Token: 50b3a5ac-6aa4-4f76-ab6d-f3132b0c4dce
     * Host: 192.168.31.158:21111
     * Accept-Encoding: gzip, deflate
     * Connection: close
     * Content-Type: multipart/form-data; boundary=--------------------------817472830912193668206114
     * Content-Length: 262
     *
     * ----------------------------817472830912193668206114
     * Content-Disposition: form-data; name="name"
     *
     * 2
     * ----------------------------817472830912193668206114
     * Content-Disposition: form-data; name="id"
     *
     * 3
     * ----------------------------817472830912193668206114--
     *
     * ############################################################################################################
     * ### 2.
     * POST /demo/request/object HTTP/1.1
     * User-Agent: PostmanRuntime/7.26.8
     * Accept:
     * Postman-Token: 5f48acc2-96fc-4cf2-9f9e-a241fd535087
     * Host: 192.168.31.158:21111
     * Accept-Encoding: gzip, deflate
     * Connection: close
     * Content-Type: application/x-www-form-urlencoded
     * Content-Length: 15
     *
     * id=123&name=235
     *
     */
    @PostMapping(value = "/request/object")
    @ResponseBody
    public DemoModel object(DemoModel demoModel)
    {
        return demoModel;
    }

    @PostMapping(value = "/request/object2")
    @ResponseBody
    public DemoModel object2(DemoModel demoModel, BlockchainModel blockchainModel)
    {
        return demoModel;
    }

    @GetMapping(value = "/request/object3")
    @ResponseBody
    public DemoModel object3(DemoModel demoModel, BlockchainModel blockchainModel)
    {
        return demoModel;
    }
}