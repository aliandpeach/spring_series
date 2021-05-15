package com.http;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.google.common.base.Objects;
import com.yk.httprequest.HttpFormDataUtil;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
        String url = "https://192.190.10.122:21112/import/upload/multiple/json";

        JSONObject jsonObject = new JSONObject();
        String fileId = UUID.randomUUID().toString().replace("-", "");
        jsonObject.put(fileId, "2.txt");

        Map<String, String> filePathMap = new HashMap<>();
        filePathMap.put(fileId, "D:\\opt\\up\\2.txt");

        String boundary = "WebKitFormBoundary2ikDa4yTuM4d47aa";
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "multipart/form-data; boundary=----" + boundary);
        HttpFormDataUtil.HttpResponse response = HttpFormDataUtil.postFormData(url, filePathMap, jsonObject.toJSONString(0), headers, false, boundary, "Content-Type: application/json");
        System.out.println(response);

    }

    @Test
    public void sendFormDataXML() throws Exception
    {
        String url = "https://192.190.116.205:443/SIMP_DBS_S/event/analyze/upload/xml";

        JAXBContext context = JAXBContext.newInstance(FileInfos.class);
        Marshaller marshaller = context.createMarshaller();

        FileInfos fileInfos = new FileInfos();
        Map<String, String> filePathMap = new HashMap<>();

        String fileId = UUID.randomUUID().toString().replace("-", "");

        filePathMap.put(fileId, "D:\\opt\\up\\2.txt");

        List<FileInfoParam> flist = new ArrayList<>();
        FileInfoParam info = new FileInfoParam();
        info.setId(fileId);
        info.setName("2.txt");
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
}