package com.yk;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class TestResource {
    public static void main(String[] args) throws IOException {
        // 带/ 从根目录位置查找，不带/, 从当前包位置查找, 该方法会先处理参数是否带/的过程，不带就找TestResource.class的全类限定名
        InputStream in1 = TestResource.class.getResource("/jdbc.properties").openStream();
        String s1 = TestResource.class.getResource("/").getPath();// path : /D:/idea_workspace/spring_series/spring_cxf_service/target/classes/
        String s2 = TestResource.class.getResource("").getPath();// path : /D:/idea_workspace/spring_series/spring_cxf_service/target/classes/com/yk

        // 默认从根目录查询，不能带/, 属于简化版的class.getResource方法
        InputStream in2 = TestResource.class.getClassLoader().getResource("jdbc.properties").openStream();
        String s4 = TestResource.class.getClassLoader().getResource("").getPath();// path : /D:/idea_workspace/spring_series/spring_cxf_service/target/classes/

        // 无法获取到getClassLoader 不可用
        // InputStream in3 = Thread.currentThread().getClass().getClassLoader().getResource("jdbc.properties").openStream();

        //必须带/，否则在处理不带/的参数的过程中，会走到java/lang/jdbc.properties (其实就是Thread类的全类限定名)
        InputStream in4 = Thread.currentThread().getClass().getResource("/jdbc.properties").openStream();
        String s5 = Thread.currentThread().getClass().getResource("/").getPath();// path : /D:/idea_workspace/spring_series/spring_cxf_service/target/classes/

        // 默认从根目录查询，不能带/, 属于简化版的class.getResource方法
        InputStream in5 = Thread.currentThread().getContextClassLoader().getResource("jdbc.properties").openStream();
        String s8 = Thread.currentThread().getContextClassLoader().getResource("").getPath();// path : /D:/idea_workspace/spring_series/spring_cxf_service/target/classes/

        Properties properties = new Properties();
        System.out.println();
    }
}
