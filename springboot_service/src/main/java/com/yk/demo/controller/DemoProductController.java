package com.yk.demo.controller;

import com.yk.demo.model.DemoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/demo")
public class DemoProductController {

    @Autowired
    private HttpServletRequest request;

    @RequestMapping(method = RequestMethod.GET, value = "/")
    @ResponseBody
    public String index() {
        return "index demo";
    }

    /**
     * 可不指定consumes,客户端可以随意指定， consumes指定为什么类型,则客户端也需指定Content-Type为什么类型
     *
     * 如指定为 application/x-www-form-urlencoded, 则客户端需指定Content-Type=application/x-www-form-urlencoded
     * 如指定为 application/json 则客户端需指定Content-Type=application/json
     * 如指定为 application/xml 则客户端需指定Content-Type=application/xml
     *
     * @ResponseBody 用于处理非view的响应，所以produces不能设置为x-www-form-urlencoded (也不是完全不行，字符串可以被返回)
     * 不加@ResponseBody的情况是响应页面
     *
     * 参数是@RequestParam("param") String param 时也一样
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/get1",  produces = "application/json")
    @ResponseBody
    public DemoModel get1(@RequestParam Map<String, String> params) {
        String contentType = request.getContentType();
        String contentTypeHeader = request.getHeader("Content-Type");
        DemoModel demoModel = new DemoModel("1", "1");
        return demoModel;
    }

    /**
     * 可不指定consumes,客户端可以随意指定, consumes指定为什么类型,则客户端也需指定Content-Type为什么类型
     * 如指定为 application/x-www-form-urlencoded, 则客户端需指定Content-Type=application/x-www-form-urlencoded
     * 如指定为 application/json 则客户端需指定Content-Type=application/json
     * 参数是@RequestParam("param") String param 时也一样
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/get11",  produces = "application/json")
    @ResponseBody
    public DemoModel get11(@RequestParam Map<String, String> params) {
        String contentType = request.getContentType();
        String contentTypeHeader = request.getHeader("Content-Type");
        DemoModel demoModel = new DemoModel("1", "1");
        return demoModel;
    }

    /**
     * 可不指定consumes, 客户端随意可指定Content-Type为 form-data x-www-form-urlencoded json text/plain application/xml等
     * springboot内部使用 MultiValueMap
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/get2", produces = "application/json")
    @ResponseBody
    public DemoModel get2(BindingAwareModelMap params) {
        String contentType = request.getContentType();
        String contentTypeHeader = request.getHeader("Content-Type");
        DemoModel demoModel = new DemoModel("1", "1");
        return demoModel;
    }

    /**
     * 使用 MultiValueMap 客户端的Content-Type只能是x-www-form-urlencoded
     *
     *
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/get3", produces = "application/json")
    @ResponseBody
    public DemoModel get3(@RequestBody MultiValueMap params) {
        String contentType = request.getContentType();
        String contentTypeHeader = request.getHeader("Content-Type");
        DemoModel demoModel = new DemoModel("1", "1");
        return demoModel;
    }

    /**
     * 参数是String时，可以指定consumes为x-www-form-urlencoded或者json，客户端保存一致，接收到的参数格式为 key=value&key=value...
     *
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/get5", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public DemoModel get5(@RequestBody String params) {
        String contentType = request.getContentType();
        String contentTypeHeader = request.getHeader("Content-Type");
        DemoModel demoModel = new DemoModel("1", "1");
        return demoModel;
    }

    /**
     * consumes不指定，客户端只能是application/json， consumes指定为x-www-form-urlencoded时， 只能接受 MultiValueMap 类型，不能是Map类型
     * (@ModelAttribute也可以，但是使用比较麻烦)
     *
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/get6", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public DemoModel get6(@RequestBody Map<String, Object> params) {
        String contentType = request.getContentType();
        String contentTypeHeader = request.getHeader("Content-Type");
        DemoModel demoModel = new DemoModel("1", "1");
        return demoModel;
    }

}
