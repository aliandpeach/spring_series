package com.reg;

import com.yk.crypto.BinHexSHAUtil;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegTest
{
    @Test
    public void test()
    {
        String __reg = "(?:(?:^.*\\.txt$)|(?:^.*\\.docx$)|(?:^.*\\.doc$))";
        String __str = "oss-cn-hangzhou-zwynet-d01-a.internet.cloud.zj.gov.cn/zjjcmspublic/jcms_files/jcms1/web2080/site/media/0/31c10184ffdf4340a2201fd4bee0edc0.mp4";
        Matcher __matcher = Pattern.compile(__reg).matcher(__str);
        boolean __find = __matcher.find();


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

        Matcher matcher6 = Pattern.compile("((?<=\\D)|(^))0[0-9]{13}((?=\\D+)|($))").matcher("07205031909406");
        while (matcher6.find())
        {
            System.out.println(matcher6.group());
        }
        Matcher matcher7 = Pattern.compile("((?<=\\D)|(^))(ZJDK|ZJBH)[0-9]{17}((?=\\D+)|($))").matcher("ZJBH33031340200210601");
        while (matcher7.find())
        {
            System.out.println(matcher7.group());
        }
        Matcher matcher8 = Pattern.compile("((?<=\\D)|(^))[BD][0-9]{6}-[0-9]{4}-[0-9]{5}((?=\\D+)|($))").matcher("B330391-2012-03145");
        while (matcher8.find())
        {
            System.out.println(matcher8.group());
        }
        System.out.println(UUID.randomUUID().toString().replace("-", ""));
        System.out.println(UUID.randomUUID().toString().replace("-", ""));
        System.out.println(UUID.randomUUID().toString().replace("-", ""));
    }

    @Test
    public void test2() throws Exception
    {
        String split = "q0x020x1010|0x10";
        String regx = "0x[0-9a-fA-F]{2}";
        Pattern pattern = Pattern.compile(regx);
        Matcher matcher = pattern.matcher(split);
        while (matcher.find())
        {
            String str = matcher.group();
            System.out.println(str);
        }

        String max_hex = Integer.toHexString(2147483647);
        int jj = Integer.parseInt("7fffffff", 16); // 最大7fffffff
        byte bb = (byte) (0xff & 0x7f);

        String _str_char = BinHexSHAUtil.hexToChar("1d");

        // 组装一个包含0x01等16进制字符的字符串
        int _i = Integer.parseInt("01", 16);
        String _str = "hello" + ((char) _i) + "|" + ((char) _i) + "world";
        System.out.print(_str);

        System.out.println();
        String _split = (((char) Integer.parseInt("01", 16)) + "\\|" + ((char) Integer.parseInt("01", 16)));
        System.out.println(_split);
        System.out.println(String.join(",", _str.split(_split)));

        System.out.println();
        ByteArrayInputStream bin = new ByteArrayInputStream(_str.getBytes(StandardCharsets.UTF_8));
        BufferedReader reader = new BufferedReader(new InputStreamReader(bin, StandardCharsets.UTF_8));
        int ch;
        while ((ch = reader.read()) != -1)
        {
            System.out.print((char) ch);
        }

        // 包含0x01字符的字符串写入文件
        FileOutputStream out = new FileOutputStream(new File("D:\\1text_" + System.currentTimeMillis() + ".txt"));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        String special = "0x01";
        int i = Integer.parseInt("01", 16);
        int j = Integer.parseInt("a0", 16);
        String str = "input" + ((char) i) + "|" + ((char) j) + "test";
        writer.write(str);
        writer.flush();

        // 包含0x01字符的字符串写入文件
        FileOutputStream _out = new FileOutputStream(new File("D:\\2text_" + System.currentTimeMillis() + ".txt"));
        int iiiii = Integer.parseInt("a0a0", 16);
        byte[] temp = new byte[]{(byte) 'i', (byte) 'n', 'p', (byte) 't', BinHexSHAUtil.intToByte(Integer.parseInt("a0", 16))};
        _out.write(temp);
        _out.flush();
    }
}
