package com.reg;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegTest
{
    @Test
    public void test()
    {
        String reg = "(金额|￥)(：|:)(([1-9]\\d*\\.?\\d*)|(0\\.\\d*[1-9]))";
        Matcher matcher = Pattern.compile(reg).matcher("金额:123多个资源的||012345修改界面表14021997831||13013013000||18729256151单动态生成，1机密陕西省莲湖区未;加机密特殊秘密aaaaaaa￥:123.2aaaaaaaaaaaaaaaaaaaaaaa字金额：123.2符验证发陕西省高新区大幅度发");
        while (matcher.find())
        {
            System.out.println(matcher.group());
        }
        String str_x = "14021997831||13013013000||18729256151";
        String ary_x[] = str_x.split("\\|\\|");
        System.out.println(ary_x);

        Matcher matcher1 = Pattern.compile("(([\\u4e00-\\u9fa5]{1,6}(省|自治区|行政区))([\\u4e00-\\u9fa5]{1,6}(市))([\\u4e00-\\u9fa5]{1,6}(区|县))([\\u4e00-\\u9fa5]{1,6}(街道|乡|镇)))|(([\\u4e00-\\u9fa5]{1,6}(省|自治区|行政区))([\\u4e00-\\u9fa5]{1,6}(市))([\\u4e00-\\u9fa5]{1,6}(区|县)))|(([\\u4e00-\\u9fa5]{1,6}(省|自治区|行政区))([\\u4e00-\\u9fa5]{1,6}(市)))|(([\\u4e00-\\u9fa5]{1,6}(市))([\\u4e00-\\u9fa5]{1,6}(区|县))([\\u4e00-\\u9fa5]{1,6}(街道|乡|镇)))|(([\\u4e00-\\u9fa5]{1,6}(区|县))([\\u4e00-\\u9fa5]{1,6}(街道|乡|镇)))|(([\\u4e00-\\u9fa5]{1,6}(市))([\\u4e00-\\u9fa5]{1,6}(区|县)))|(([\\u4e00-\\u9fa5]{1,6}(省|自治区|行政区))([\\u4e00-\\u9fa5]{1,6}(市))([\\u4e00-\\u9fa5]{1,6}(街道|乡|镇)))|(([\\u4e00-\\u9fa5]{1,6}(省|自治区|行政区))([\\u4e00-\\u9fa5]{1,6}(区|县))([\\u4e00-\\u9fa5]{1,6}(街道|乡|镇)))|(([\\u4e00-\\u9fa5]{1,6}(省|自治区|行政区))([\\u4e00-\\u9fa5]{1,6}(区|县)))|(([\\u4e00-\\u9fa5]{1,6}(市))([\\u4e00-\\u9fa5]{1,6}(街道|乡|镇)))|(([\\u4e00-\\u9fa5]{1,6}(省|自治区|行政区))([\\u4e00-\\u9fa5]{1,6}(街道|乡|镇)))").matcher("我叫涉敏敏仙居县，身份证号码是：320320202003033023，手机号是 13013013000，有事请电联。多个资源的修改界18729256151发顺丰面表单动14021997831||13013013000||18729256151态生成，1机密陕西省西安市莲湖区未;加机密特殊秘密aaaaaaa￥:123.2aaaaaaaaaaaaaaaaaaaaaaa字金额：123.2符验证发陕西省高新区大幅度发18729256151发顺丰");
        while (matcher1.find())
        {
            System.out.println(matcher1.group());
        }

        Matcher matcher2 = Pattern.compile("((?<=\\D)|(^))1[3|4|5|7|8][0-9]{9}((?=\\D+)|($))").matcher("测试18729256151测试");
        while (matcher2.find())
        {
            System.out.println(matcher2.group());
        }

        Matcher matcher3 = Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}\\s[0-9]{2}:[0-9]{2}:[0-9]{2},[0-9]{2,3}\\s\\[0x[0-9a-z]{12}\\]\\s{1,2}ERROR")
                .matcher("2022-02-23 01:43:53,992 [0x7ff7535628c0] ERROR main.cpp(39) checkRegExp - Starting the MessageListener application!");
        while (matcher3.find())
        {
            System.out.println(matcher3.group());
        }

        Matcher matcher4 = Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}\\s[0-9]{2}:[0-9]{2}:[0-9]{2},[0-9]{2,3}\\s{1,2}ERROR\\s+\\[")
                .matcher("2022-02-23 01:44:32,259  ERROR    [      MessageFactory.py -- 76   ]   [Th:MainThread          -- 14008925321425200]");
        while (matcher4.find())
        {
            System.out.println(matcher4.group());
        }

        Matcher matcher5 = Pattern.compile("^\\[[0-9]{4}-[0-9]{2}-[0-9]{2}\\s[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{2,3}\\s{1,2}INFO\\s{1,2}\\]")
                .matcher("[2022-03-23 15:03:31.376 INFO ] [main-listener-0] {com.yk.base.config.ApplicationStartListener:40} - spring docker service running \n" +
                        "jdbc:mysql://127.0.0.1:3307/demo?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false&allowMultiQueries=true\n");
        while (matcher5.find())
        {
            System.out.println(matcher5.group());
        }
    }
}
