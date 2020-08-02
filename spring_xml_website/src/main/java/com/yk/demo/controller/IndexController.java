package com.yk.demo.controller;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Controller
@RequestMapping("/")
public class IndexController {
    private Logger logger = LoggerFactory.getLogger("demo");

    /*@RequestMapping(value = "/index")
    public String index(HttpServletRequest request) {
        return "index";
    }*/

    @RequestMapping("/index")
    public String test(ModelMap map) {
        map.put("thText", "设置文本内容");
        map.put("thUText", "设置文本内容");
        map.put("thValue", "设置当前元素的value值");
        map.put("thEach", Arrays.asList("列表", "遍历列表"));
        map.put("thIf", "msg is not null");
        map.put("thObject", new UserEntity("sadfa", "asfasfd", "asfsaf", "asdfasf", "saf", "asfd", "sadf", 1));
        return "index";
    }

    @Data
    private class UserEntity {
        private String a;
        private String b;
        private String c;
        private String d;
        private String e;
        private String f;
        private String g;
        private int i;

        public UserEntity(String a, String b, String c, String d, String e, String f, String g, int i) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
            this.e = e;
            this.f = f;
            this.g = g;
            this.i = i;
        }
    }
}