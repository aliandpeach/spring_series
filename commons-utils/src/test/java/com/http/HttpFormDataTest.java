package com.http;

import cn.hutool.json.JSONObject;
import com.google.common.base.Objects;
import com.yk.httprequest.HttpClientUtil;
import com.yk.httprequest.HttpFormDataUtil;
import com.yk.httprequest.JSONUtil;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * HttpFormDataTest
 *
 * @author yangk
 * @version 1.0
 * @since 2021/4/28 16:50
 */

public class HttpFormDataTest
{
    /**
     * Content-Type: multipart/form-data; boundary=----WebKitFormBoundarykHWy2Qaa9Q8z5JJi
     * <p>
     * ------WebKitFormBoundarykHWy2Qaa9Q8z5JJi
     * Content-Disposition: form-data; name="files1"; filename="1.txt"
     * Content-Type: text/plain
     * <p>
     * 111111
     * ------WebKitFormBoundarykHWy2Qaa9Q8z5JJi
     * Content-Disposition: form-data; name="files2"; filename="2.txt"
     * Content-Type: text/plain
     * <p>
     * 222222
     * ------WebKitFormBoundarykHWy2Qaa9Q8z5JJi
     * Content-Disposition: form-data; name="params"; filename="blob"
     * Content-Type: application/json
     * <p>
     * {"id":"1619604419314","fileName":"name1"}
     * ------WebKitFormBoundarykHWy2Qaa9Q8z5JJi--
     */
    @Test
    public void sendFormData() throws Exception
    {
        String url = "https://192.190.10.122:21111/import/upload/multiple/json";

        JSONObject jsonObject = new JSONObject();
        String fileId = UUID.randomUUID().toString().replace("-", "");
        jsonObject.put(fileId, "5-1.txt");

        Map<String, String> filePathMap = new HashMap<>();
        filePathMap.put(fileId, "F:\\test_share_dir\\5\\5-1.txt");

        String boundary = "WebKitFormBoundary2ikDa4yTuM4d47aa";
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "multipart/form-data; boundary=----" + boundary);
        HttpFormDataUtil.HttpResponse response = HttpFormDataUtil.postFormData(url, filePathMap, jsonObject.toJSONString(0), headers, false, boundary, "Content-Type: application/json");
        System.out.println(response);

    }

    @Test
    public void sendFormDataXML() throws Exception
    {
        String url = "https://192.190.116.205:443/SIMP_DBS_S/event/file/analysis/upload/xml";

        JAXBContext context = JAXBContext.newInstance(FileInfos.class);
        Marshaller marshaller = context.createMarshaller();

        FileInfos fileInfos = new FileInfos();
        Map<String, String> filePathMap = new HashMap<>();

        String fileId = UUID.randomUUID().toString().replace("-", "");

        filePathMap.put(fileId, "F:\\test_share_dir\\5\\5-1.txt");

        List<FileInfoParam> flist = new ArrayList<>();
        FileInfoParam info = new FileInfoParam();
        info.setId(fileId);
        info.setName("5-1.txt");
        flist.add(info);
        fileInfos.setFileInfoParamList(flist);

        String str;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream())
        {
            marshaller.marshal(fileInfos, outputStream);
            str = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
        }

        String boundary = "WebKitFormBoundary2ikDa4yTuM4d47aa";
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "multipart/form-data; boundary=----" + boundary);
        HttpFormDataUtil.HttpResponse response = HttpFormDataUtil.postFormData(url, filePathMap, str, headers, true, boundary, "Content-Type: application/xml");
        System.out.println(response);

    }
    @Test
    public void sendFormDataXMLByHttpClient() throws Exception
    {
        String url = "https://192.190.116.205:443/SIMP_DBS_S/event/file/analysis/upload/xml";

        JAXBContext context = JAXBContext.newInstance(FileInfos.class);
        Marshaller marshaller = context.createMarshaller();

        FileInfos fileInfos = new FileInfos();
        Map<String, String> filePathMap = new HashMap<>();

        String fileId = UUID.randomUUID().toString().replace("-", "");

        filePathMap.put(fileId, "F:\\test_share_dir\\5\\5-1.txt");

        List<FileInfoParam> flist = new ArrayList<>();
        FileInfoParam info = new FileInfoParam();
        info.setId(fileId);
        info.setName("5-1.txt");
        flist.add(info);
        fileInfos.setFileInfoParamList(flist);

        String str;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream())
        {
            marshaller.marshal(fileInfos, outputStream);
            str = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
        }

        String boundary = UUID.randomUUID().toString().replace("-", "");
        Map<String, Object> headers = new HashMap<>();
//        headers.put("Content-Type", "multipart/form-data; boundary=----" + boundary);
        HttpClientUtil.ProxyInfo proxyInfo = new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8080, "http");
        HttpFormDataUtil.HttpResponse response = HttpFormDataUtil
                .postFormDataByHttpClient(url, filePathMap, str, headers, proxyInfo, boundary, "params", "application/xml");
        System.out.println(response);

    }
    @Test
    public void sendFormDataJson() throws Exception
    {
        String url = "https://192.190.10.122:21111/import/upload/multiple/json";
        Map<String, String> filePathMap = new HashMap<>();

        String fileId = UUID.randomUUID().toString().replace("-", "");

        filePathMap.put(fileId, "D:\\workspace\\SIMP_DBS_D_\\SIMPLE-DBS-SDK\\src\\main\\resources\\yangkai\\sdk_pub.pem");

        String str = JSONUtil.toJson(new HashMap<String, String>(Collections.singletonMap(fileId, "5-1.txt")));

        String boundary = UUID.randomUUID().toString().replace("-", "");
        Map<String, Object> headers = new HashMap<>();
//        headers.put("Content-Type", "multipart/form-data; boundary=----" + boundary);
        HttpClientUtil.ProxyInfo proxyInfo = new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8080, "http");
        HttpFormDataUtil.HttpResponse response = HttpFormDataUtil.postFormDataByHttpClient(url, filePathMap, str, headers, proxyInfo, boundary, "params", "application/json");
        System.out.println(response);

    }

    /**
     * 上传文件接口, 附带json格式的参数
     */
    @Test
    public void sendFormDataJson3() throws Exception
    {
        String url = "https://192.190.10.122:21111/import/upload/multiple/json3";
        Map<String, String> filePathMap = new HashMap<>();

        String fileId = UUID.randomUUID().toString().replace("-", "");

        filePathMap.put(fileId, "D:\\workspace\\SIMP_DBS_D_\\SIMPLE-DBS-SDK\\src\\main\\resources\\yangkai\\sdk_pub.pem");

        String str = JSONUtil.toJson(new HashMap<String, String>(Collections.singletonMap(fileId, "5-1.txt")));

        String boundary = UUID.randomUUID().toString().replace("-", "");
        Map<String, Object> headers = new HashMap<>();
//        headers.put("Content-Type", "multipart/form-data; boundary=----" + boundary);
        HttpClientUtil.ProxyInfo proxyInfo = new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8080, "http");
        HttpFormDataUtil.HttpResponse response = HttpFormDataUtil.postFormDataByHttpClient(url, filePathMap, str, headers, proxyInfo, boundary, "params", "application/json");
        System.out.println(response);

    }
    /**
     * 上传文件接口, 附带json格式的参数
     */
    @Test
    public void sendFormDataJson4() throws Exception
    {
        String url = "https://192.190.10.122:21111/import/upload/multiple/json4";
        Map<String, String> filePathMap = new HashMap<>();

        String fileId = UUID.randomUUID().toString().replace("-", "");

        filePathMap.put(fileId, "D:\\workspace\\SIMP_DBS_D_\\SIMPLE-DBS-SDK\\src\\main\\resources\\yangkai\\sdk_pub.pem");

        String str = JSONUtil.toJson(new HashMap<String, String>(Collections.singletonMap(fileId, "sdk_pub.txt")));

        String boundary = UUID.randomUUID().toString().replace("-", "");
        Map<String, Object> headers = new HashMap<>();
//        headers.put("Content-Type", "multipart/form-data; boundary=----" + boundary);
        HttpClientUtil.ProxyInfo proxyInfo = new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8080, "http");
        HttpFormDataUtil.HttpResponse response = HttpFormDataUtil
                .postFormDataByHttpClient(url, filePathMap, str, headers, proxyInfo, boundary, "level", "text/plain");
        System.out.println(response);

    }

    @Test
    public void sendFormDataJson5() throws Exception
    {
        String url = "https://192.190.10.122:21111/import/upload/multiple/json5";
        Map<String, String> filePathMap = new HashMap<>();

        String fileId1 = UUID.randomUUID().toString().replace("-", "");

        filePathMap.put(fileId1, "C:\\Users\\yk\\Desktop\\1.txt");
        String fileId2 = UUID.randomUUID().toString().replace("-", "");

        filePathMap.put(fileId2, "C:\\Users\\yk\\Desktop\\2.txt");

        Map<String, String> map = new HashMap<String, String>(Collections.singletonMap("name", "sdk_pub.txt"));
        map.put("value", fileId1);
        String str = JSONUtil.toJson(map);

        String boundary = UUID.randomUUID().toString().replace("-", "");
        Map<String, Object> headers = new HashMap<>();
//        headers.put("Content-Type", "multipart/form-data; boundary=----" + boundary);
        HttpClientUtil.ProxyInfo proxyInfo = new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8080, "http");
        HttpFormDataUtil.HttpResponse response = HttpFormDataUtil
                .postFormDataByHttpClient(url, filePathMap, str, headers, proxyInfo, boundary, "item", "application/json");
        System.out.println(response);

    }
    @Test
    public void sendFormDataJson6() throws Exception
    {
        String url = "https://192.190.10.122:21111/import/upload/multiple/json6";
        Map<String, String> filePathMap = new HashMap<>();

        String fileId = UUID.randomUUID().toString().replace("-", "");

        filePathMap.put(fileId, "D:\\workspace\\SIMP_DBS_D_\\SIMPLE-DBS-SDK\\src\\main\\resources\\yangkai\\sdk_pub.pem");

        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> map = new HashMap<String, String>(Collections.singletonMap("name", "sdk_pub.txt"));
        map.put("value", fileId);
        list.add(map);
        String str = JSONUtil.toJson(list);

        String boundary = UUID.randomUUID().toString().replace("-", "");
        Map<String, Object> headers = new HashMap<>();
//        headers.put("Content-Type", "multipart/form-data; boundary=----" + boundary);
        HttpClientUtil.ProxyInfo proxyInfo = new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8080, "http");
        HttpFormDataUtil.HttpResponse response = HttpFormDataUtil
                .postFormDataByHttpClient(url, filePathMap, str, headers, proxyInfo, boundary, "items", "application/json");
        System.out.println(response);

    }

    @Test
    public void sendPostBytes() throws Exception
    {
        String content = "abc123中文";
        HttpFormDataUtil.HttpResponse response = HttpFormDataUtil.postText("https://192.190.10.122:21112/import/upload/multiple/bytes", content);
        System.out.println(response);
    }

    @XmlRootElement(name = "files")
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class FileInfos
    {
        @XmlElement(name = "file")
        private List<FileInfoParam> fileInfoParamList;

        public List<FileInfoParam> getFileInfoParamList()
        {
            return fileInfoParamList;
        }

        public void setFileInfoParamList(List<FileInfoParam> fileInfoParamList)
        {
            this.fileInfoParamList = fileInfoParamList;
        }
    }


    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class FileInfoParam
    {
        private String id;

        private String name;

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FileInfoParam that = (FileInfoParam) o;
            return Objects.equal(id, that.id);
        }

        @Override
        public int hashCode()
        {
            return Objects.hashCode(id);
        }

        public String getId()
        {
            return id;
        }

        public void setId(String id)
        {
            this.id = id;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }
    }

    /**
     * 上传文件接口, 附带json格式的参数
     */
    @Test
    public void getHtml() throws Exception
    {
        String url = "https://192.190.116.205/temporary_upload/bmj-new-install-1.5.0.0.7.v5.zip";
        new HttpClientUtil().getBytes(url, new HashMap<>(), new HashMap<>(), "a.html", System.currentTimeMillis() + "", "F:\\iworkspace\\_downloadx");
    }
}