package com.http;

import com.yk.httprequest.HttpClientUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/07/30 13:59:14
 */
public class WebserviceTest
{
    @Test
    public void exchange() throws GeneralSecurityException, IOException
    {
        String requestBody = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<soap:Envelope xmlns:m=\"http://schemas.microsoft.com/exchange/services/2006/messages\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:t=\"http://schemas.microsoft.com/exchange/services/2006/types\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "\t<soap:Header>\n" +
                "\t\t<t:RequestServerVersion Version=\"Exchange2007\"/>\n" +
                "\t</soap:Header>\n" +
                "\t<soap:Body>\n" +
                "\t\t<m:FindFolder Traversal=\"Deep\">\n" +
                "\t\t\t<m:FolderShape>\n" +
                "\t\t\t\t<t:BaseShape>IdOnly</t:BaseShape>\n" +
                "\t\t\t\t<t:AdditionalProperties>\n" +
                "\t\t\t\t\t<t:FieldURI FieldURI=\"folder:DisplayName\"/>\n" +
                "\t\t\t\t\t<t:FieldURI FieldURI=\"folder:FolderClass\"/>\n" +
                "\t\t\t\t\t<t:FieldURI FieldURI=\"folder:ChildFolderCount\"/>\n" +
                "\t\t\t\t\t<t:FieldURI FieldURI=\"folder:ParentFolderId\"/>\n" +
                "\t\t\t\t\t<t:FieldURI FieldURI=\"folder:EffectiveRights\"/>\n" +
                "\t\t\t\t</t:AdditionalProperties>\n" +
                "\t\t\t</m:FolderShape>\n" +
                "\t\t\t<m:IndexedPageFolderView BasePoint=\"Beginning\" MaxEntriesReturned=\"100\" Offset=\"0\"/>\n" +
                "\t\t\t<m:ParentFolderIds>\n" +
                "\t\t\t\t<t:DistinguishedFolderId Id=\"msgfolderroot\"/>\n" +
                "\t\t\t</m:ParentFolderIds>\n" +
                "\t\t</m:FindFolder>\n" +
                "\t</soap:Body>\n" +
                "</soap:Envelope>";

        HttpClientUtil.Config config = new HttpClientUtil.Config();
        URL url = new URL("https://192.168.37.104/ews/Exchange.asmx");
        config.ofCredentials(new HttpClientUtil.Credentials("administrator", "Spinfo0123", url.getProtocol()));
        CloseableHttpClient client = HttpClientUtil.getClient(config);
        HttpPost httpPost = new HttpPost("https://192.168.37.104/ews/Exchange.asmx");

//        HttpHost proxyHost = new HttpHost("127.0.0.1", 8080);
//        httpPost.setConfig(RequestConfig.custom()
//                .setConnectTimeout(22000).setConnectionRequestTimeout(12000)
//                .setSocketTimeout(24000).setProxy(proxyHost).build());

        httpPost.addHeader("Content-type", "text/xml; charset=utf-8");
        httpPost.addHeader("User-Agent", "webservice");
        httpPost.addHeader("Accept", "text/html; charset=utf-8");
        httpPost.addHeader("Keep-Alive", "300");
        httpPost.addHeader("Connection", "Keep-Alive");
//        httpPost.addHeader("SOAPAction", "https://ttt/test.aspx/methodname");

        EntityBuilder builder = EntityBuilder.create();
        builder.setBinary(requestBody.getBytes(StandardCharsets.UTF_8));
        builder.setContentType(ContentType.create("text/xml", StandardCharsets.UTF_8));
        builder.setContentEncoding(StandardCharsets.UTF_8.name());
        httpPost.setEntity(builder.build());

//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        HttpClientUtil.ByteArrayRequestEntity byteArrayRequestEntity = new HttpClientUtil.ByteArrayRequestEntity(out);
//        out.write(requestBody.getBytes(StandardCharsets.UTF_8));
//        httpPost.setEntity(byteArrayRequestEntity);

        CloseableHttpResponse response = client.execute(httpPost);
        int status = response.getStatusLine().getStatusCode();
        InputStream content = response.getEntity().getContent();
        StringWriter writer = new StringWriter();
        IOUtils.copy(content, writer, StandardCharsets.UTF_8);
        System.out.println(status + " : " + writer.toString());
    }
}
