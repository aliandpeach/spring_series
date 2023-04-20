package com.http;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Objects;
import com.yk.httprequest.HttpClientUtil;
import com.yk.httprequest.HttpFormDataUtil;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HttpClientDataTest
{
    @Test
    public void sendRequestBodyXml() throws Exception
    {
        String url = "https://192.168.31.158:21111/demo/request/body/xml";
        HttpClientUtil httpClientUtil = new HttpClientUtil(new HttpClientUtil.Config().ofProxy(new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8089, "http")));

        FileInfos body = new FileInfos();
        FileInfoParam file = new FileInfoParam();
        String fileId = UUID.randomUUID().toString().replace("-", "");
        file.setId(fileId);
        file.setName(fileId + ".name");
        body.setFileInfoParamList(Collections.singletonList(file));

        JAXBContext context = JAXBContext.newInstance(FileInfos.class);
        Marshaller marshaller = context.createMarshaller();
        String str;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream())
        {
            marshaller.marshal(body, outputStream);
            str = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
        }

        String infos = httpClientUtil.postXml(url, new HashMap<>(), str);
        System.out.println(infos);
    }

    @Test
    public void sendRequestObject() throws Exception
    {
        String url = "https://192.168.31.158:21111/demo/request/object";
        HttpClientUtil httpClientUtil = new HttpClientUtil(new HttpClientUtil.Config().ofProxy(new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8089, "http")));

        Map<String, String> body = new HashMap<>();
        body.put("id", "1");
        body.put("name", "1");
        Object infos = httpClientUtil.postFormData(url, new HashMap<>(), body, "", new TypeReference<Object>()
        {
        });
        System.out.println(infos);

        body.put("id", "2");
        body.put("name", "2");
        infos = httpClientUtil.postFormUrlencoded(url, new HashMap<>(), body, new TypeReference<Object>()
        {
        });
        System.out.println(infos);
    }

    @Test
    public void sendRequestPartObject() throws Exception
    {
        String url = "https://192.168.31.158:21111/demo/request/part/object";
        Map<String, Object> demo = new HashMap<>();
        demo.put("id", 1);
        demo.put("name", 12);

        String boundary = UUID.randomUUID().toString().replace("-", "");
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "multipart/form-data; boundary=----" + boundary);
        HttpClientUtil.ProxyInfo proxyInfo = new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8089, "http");
        HttpFormDataUtil.HttpResponse response = HttpFormDataUtil.postFormData(url,
                new HashMap<>(),
                Collections.singletonMap("demoModel", JSONUtil.toJsonStr(demo)),
                headers,
                proxyInfo,
                boundary,
                "application/json");
        System.out.println(response);
    }

    @Test
    public void multipleUploadValidatedItem() throws Exception
    {
        String url = "https://192.168.31.158:21111/import/upload/multiple/validated/item";

        Map<String, String> nameWithContent = new HashMap<>();
        nameWithContent.put("name", "env.txt1");
        nameWithContent.put("value", "env.txt2");

        Map<String, String> headers = new HashMap<>();

        HttpClientUtil httpClientUtil = new HttpClientUtil(new HttpClientUtil.Config().ofProxy(new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8089, "http")));
        Map<String, Object> response = httpClientUtil.postFormData(url, headers, nameWithContent, "D:\\env.txt", new TypeReference<Map<String, Object>>()
        {
        });
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
}
