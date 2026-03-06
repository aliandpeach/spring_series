package com.http;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Objects;
import com.yk.httprequest.HttpClientUtil;
import com.yk.httprequest.HttpFormDataUtil;
import com.yk.test.ws.sm.WSSmCommUpper;
import com.yk.util.ConvertUtil;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transport.http.URLConnectionHTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HttpClientDataTest
{

    public static void ignoreSSL() throws Exception
    {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager()
                {
                    public X509Certificate[] getAcceptedIssuers()
                    {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType)
                    {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType)
                    {
                    }
                }
        };

        SSLContext sc = SSLContext.getInstance("TLSv1.2");
        sc.init(null, trustAllCerts, new SecureRandom());

        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
    }

    @Test
    public void testDynamicClient() throws Exception
    {
        ignoreSSL();
        JaxWsDynamicClientFactory dynamicClientFactory = JaxWsDynamicClientFactory.newInstance();
        String address = "http://127.0.0.1:10081/WSSmCommUpper/WSSmCommUpper?wsdl";
        try (Client client = dynamicClientFactory.createClient(address))
        {
            URLConnectionHTTPConduit conduit = (URLConnectionHTTPConduit) client.getConduit();

            TLSClientParameters tlsParams = new TLSClientParameters();
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                    }
            };
            tlsParams.setTrustManagers(trustAllCerts);
            tlsParams.setDisableCNCheck(true); // 禁用主机名验证
            conduit.setTlsClientParameters(tlsParams);

            HTTPClientPolicy policy = new HTTPClientPolicy();
            long timeout = 2000;//超时时间3秒
            policy.setConnectionTimeout(timeout);
            policy.setReceiveTimeout(timeout);
            conduit.setClient(policy);
            Object[] resObjArray = client.invoke("reportHealthView", "status", "status", "status");
            System.out.println(resObjArray);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void testQName()
    {
        System.setProperty("http.proxyPort", "10082");
        System.setProperty("http.proxyHost", "127.0.0.1");
        try
        {
            // WSDL 地址
            URL wsdlURL = new URL("http://127.0.0.1:1443/webService/WSSmCommUpper/WSSmCommUpper?wsdl");
            // 服务 QName
            QName qname = new QName("http://wssmcommupper/", "WSSmCommUpperImplService");
            // 创建服务
            Service service = Service.create(wsdlURL, qname);
            // 获取端口
            WSSmCommUpper wsSmCommUpper = service.getPort(WSSmCommUpper.class);
            // 调用服务
            int result = wsSmCommUpper.reportHealthView("string", "string", "string");
            System.out.println("Result: " + result);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void testProxy()
    {
        try
        {
            // 创建 CXF 代理工厂
            JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
            factory.setServiceClass(WSSmCommUpper.class);
            factory.setAddress("http://127.0.0.1:1443/webService/WSSmCommUpper/WSSmCommUpper?wsdl");
            // 设置编码
            factory.getOutInterceptors().add(new org.apache.cxf.interceptor.LoggingOutInterceptor());
            factory.getInInterceptors().add(new org.apache.cxf.interceptor.LoggingInInterceptor());

            // 设置超时
            Client proxy = factory.getClientFactoryBean().create();
            HTTPConduit conduit = (HTTPConduit) proxy.getConduit();

            // 配置超时（单位：毫秒）
            conduit.getClient().setConnectionTimeout(5000); // 连接超时
            conduit.getClient().setReceiveTimeout(10000);   // 响应超时

            // 创建客户端
            WSSmCommUpper client = (WSSmCommUpper) factory.create();
            // 调用服务
            int result = client.reportHealthView("string", "string", "string");
            System.out.println("Result: " + result);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void testProxyByHttp() throws Exception
    {
        String soapBodyXml = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><ns2:reportHealthView xmlns:ns2=\"http://wssmcommupper/\"><arg0>string</arg0><arg1>string</arg1><arg2>string</arg2></ns2:reportHealthView></soap:Body></soap:Envelope>";
        String url = "http://127.0.0.1:10081/WSSmCommUpper/WSSmCommUpper?wsdl";
        try (CloseableHttpClient httpClient = HttpClients.createDefault())
        {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Accept", "text/xml, application/json, text/plain, */*");
            String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36";
            httpPost.setHeader("User-Agent", userAgent);
            httpPost.setHeader("Connection", "keep-alive");

            EntityBuilder builder = EntityBuilder.create();
            builder.setText(soapBodyXml);
            builder.setContentType(ContentType.APPLICATION_XML);
            builder.setContentEncoding(StandardCharsets.UTF_8.name());
            httpPost.setEntity(builder.build());
            String responseString = EntityUtils.toString(httpClient.execute(httpPost).getEntity());
            System.out.println(responseString);
        }

        HttpClientUtil httpClientUtil = new HttpClientUtil();
        String responseString = httpClientUtil.postXml(url, new HashMap<>(), soapBodyXml);
        System.out.println(responseString);
    }

    /*@Test
    public void test()
    {
        WSSmCommUpperImplService wsSmCommUpperImplService = new WSSmCommUpperImplService();
        int result = wsSmCommUpperImplService.getWSSmCommUpperImplPort().reportHealthView("string", "string", "string");
        System.out.println("Result: " + result);
    }*/

    String body = "{\"charSet\":\"UTF-8\",\"code\":0,\"deviceId\":\"SIMP-ANDLPS@SIMP-ANDLP-S44-7000\",\"encodeData\":[17,121,-69,22,-52,-103,-8,46,-64,93,115,82,56,11,-85,-26,-71,95,-95,-88,-60,35,6,-48,-109,-67,-20,78,-68,109,36,-7,70,32,60,-62,-48,-17,69,73,124,-19,75,-48,-25,111,-94,80,78,-124,-85,29,31,22,6,-11,-84,-23,-112,98,-106,88,8,90,-36,-12,123,27,-83,-31,-124,-30,58,14,44,-55,78,121,126,57,-59,96,-44,-80,-33,-52,10,117,-91,-28,-123,14,-123,-75,-48,-83,-99,90,-89,-69,69,34,-63,-102,112,79,27,35,-73,119,-75,-70,60,-110,-22,-111,-103,-63,19,-121,1,-16,-26,-57,-63,3,-105,51,46,-38,-52,114,17,95,-105,-96,-39,-16,-58,-127,64,-77,-57,32,-32,-91,85,66,-55,-115,-19,72,-100,-101,-46,-101,-112,98,119,-35,31,-33,93,-41,-90,80,-100,112,6,5,7,-87,58,-6,-117,-74,88,78,-61,-43,16,29,34,-125,-96,-122,-13,-30,97,108,2,95,-102,105,-66,-104,30,53,5,-27,93,-91,-84,26,11,53,13,52,113,-74,43,123,9,-55,-113,-77,-12,-4,25,56,56,69,-81,-70,-58,-76,-100,-115,80,-123,106,72,-57,-91,-99,-59,5,56,-89,76,85,-37,-49,-26,93,-53,-35,-107,-59,102,-49,-57,-56,-39,47,-2,92,105,124,74,-88,19,71,80,126,29,51,-105,16,62,63,57,-75,-99,-24,-121,-110,-92,-118,33,43,114,25,-96,-78,17,16,10,48,52,43,56,-29,89,-65,-102,-37,126,111,-88,-20,-60,94,-65,53,74,113,-113,21,73,-35,96,4,-54,35,-71,-42,35,62,-35,-73,93,-21,-35,7,-36,98,111,23,-72,80,69,83,54,-88,50,-51,-119,-61,59,-71,92,14,71,-10,26,127,-118,1,-111,61,114,-1,76,-24,-54,-102,-93,65,-103,-23,-38,69,-113,97,79,101,114,38,-54,79,54,86,-123,-89,0,66,96,-107,-26,87,-112,41,66,26,109,-126,-120,93,75,18,-1,2,26,12,-37,78,-46,-101,108,35,124,-86,-2,44,-53,-119,19,-86,-102,20,14,-88,105,125,27,-35,-68,90,5,-34,-120,-100,19,42,-66,70,-57,-114,-77,-13,-94,-38,-123,47,106,-27,91,107,-8,-69,42,-4,47,85,-21,-126,55,76,-16,-116,-109,62,-57,-4,-5,-93,10,81,16,50,-125,97,-1,122,-44,85,13,13,44,-35,-95,-46,-63,-114,-12,63,83,55,-23,-33,27,-65,48,-94,-58,-59,-36,37,-123,62,9,-80,-103,-79,-37,16,90,108,54,-51,51,125,13,-17,75,-8,-45,24,-103,-14,11,40,93,-23,-82,-26,96,108,4,-25,50,-64,40,-100,-6,-101,69,-23,-92,18,21,-45,-81,-124,-56,-97,-116,-48,24,-111,-41,78,-123,-55,-119,-36,27,-128,-110,-47,120,-52,-37,96,-105,66,35,39,-45,-17,-104,-113,29,82,37,5,0,87,45,33,63,53,79,21,110,92,-12,-110,60,30,-10,65,-5,126,-98,58,97,76,46,-103,77,-52,63,125,74,73,77,-102,-49,-65,-31,-31,-35,82,-57,41,-50,26,-41,84,-54,8,-122,-103,-89,94,-44,13,53,25,39,64,-118,-67,15,-70,75,-121,38,107,-37,-50,30,50,11,-39,-52,45,-26,107,-89,105,30,40,36,-42,-39,-64,92,75,-12,108,-111,112,-97,-81,-29,29,-49,113,3,109,11,-84,-3,-19,-33,-1,2,10,-31,-122,-76,84,-22,-68,98,-75,-114,22,-100,-31,104,94,34,-121,35,78,-99,0,29,66,87,6,99,3,-38,0,8,-93,-51,-89,-110,-36,53,-28,-93,7,-36,104,90,-11,4,-46,-117,60,-31,113,12,-107,120,-19,29,-50,2,-1,-65,-76,-79,45,-19,76,64,-92,49,62,-48,122,112,22,-40,36,22,0,122,63,14,0,23,5,52,-126,-128,-111,109,48,49,120,66,-123,-82,42,-43,77,15,71,-77,45,98,-83,-128,-25,95,48,-19,-97,-8,45,91,-77,-110,72,-3,78,-41,99,-11,-35,-79,-5,-14,47,7,-125,90,-38,-25,-24,91,-112,-5,127,-105,77,84,-14,41,18,-41,39,-20,-17,-41,44,-98,64,35,-61,51,44,-9,-117,-81,-8,55,-66,67,3,-50,119,40,-113,119,28,-89,-68,-115,-31,120,-67,36,10,66,-87,65,73,-114,-22,-15,57,-22,-110,1,-55,-70,-60,-66,-57,41,93,84,61,117,-27,17,76,-14,-125,-48,-94,-3,112,-104,-39,-69,-89,-127,-95,123,-40,-27,-14,73,42,21,-31,99,-77,-94,102,-98,-61,33,-35,63,17,62,-70,10,-106,-105,-105,101,51,14,5,24,-32,-69,15,18,-88,31,-55,-47,-15,112,65,4,1,-3,-96,96,65,-46,79,84,-69,-77,35,27,-50,-90,3,97,-95,-109,-5,8,123,109,-15,39,-65,-50,88,50,54,-71,22,-127,33,-43,-99,41,33,48,0,24,-58,42,89,-87,121,-11,124,74,-78,54,29,47,-61,66,-59,-110,-26,64,107,48,-35,80,8,-10,124,97,-38,22,-37,5,101,106,-78,67,93,2,46,112,-45,-95,88,63,12,-45,-66,91,-86,-32,46,84,-50,-102,104,1,29,-31,117,49,49,68,10,-70,-76,-45,74,85,10,-28,35,28,71,30,-39,-61,-5,118,114,-24,6,9,-82,93,107,54,0,104,-58,-8,26,120,50,-107,-121,-58,105,4,34,-17,118,-6,-47,17,27,-60,77,-14,-24,36,-127,-50,-122,61,-11,-105,-61,15,102,-50,43,60,124,82,-14,-124,-32,-98,-48,113,-17,-45,117,90,1,-65,-97,41,110,-102,-80,-46,-123,-100,122,22,-118,60,30,-56,-40,-25,72,19,116,-30,84,117,-37,-91,-92,94,46,-51,116,-23,65,50,85,-11,-18,-49,92,-124,-47,85,19,-54,67,98,-34,94,118,35,-35,124,-45,-4,-65,7,51,-76,-1,51],\"logXml\":\"\",\"msg\":\"\",\"oprCode\":\"Operation_Log_1.00\"}";

    @Test
    public void baseHttpClientTest() throws Exception
    {
        String url = "http://172.30.207.182:80/secaudit/api/reportLog";
        try (CloseableHttpClient httpClient = HttpClients.createDefault())
        {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Host", "172.30.207.182"); // Host千万不能写成http://ip:port
            httpPost.setHeader("Accept", "text/xml, application/json, text/plain, */*");
            String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36";
            httpPost.setHeader("User-Agent", userAgent);
            httpPost.setHeader("Connection", "keep-alive");

            EntityBuilder builder = EntityBuilder.create();
            builder.setText(body);
            builder.setContentType(ContentType.APPLICATION_JSON);
            builder.setContentEncoding(StandardCharsets.UTF_8.name());
            httpPost.setEntity(builder.build());
            String responseString = EntityUtils.toString(httpClient.execute(httpPost).getEntity());
            System.out.println(responseString);
        }
    }

    @Test
    public void baseHttpClientTest2() throws Exception
    {
//        List<String> numbers = IntStream.range(100000, 1000000).mapToObj(num -> "178" + String.format("%6d", num) + "58").collect(Collectors.toList());
//        List<List<String>> partitions = ConvertUtil.partition(numbers, 50000);
//        partitions.add(IntStream.range(71371, 100000).mapToObj(num -> "178" + String.format("%6d", num) + "58").collect(Collectors.toList()));

        List<Integer> _numbers = IntStream.range(100000, 1000000).boxed().collect(Collectors.toList());
        List<List<Integer>> partitions = ConvertUtil.partition(_numbers, 50000);
        partitions.add(IntStream.range(71371, 100000).boxed().collect(Collectors.toList()));

        ExecutorService service = Executors.newFixedThreadPool(5);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (List<Integer> numbers : partitions)
        {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() ->
            {
                List<String> phones = numbers.stream().map(num -> "178" + String.format("%6d", num) + "58").collect(Collectors.toList());
                int min1 = numbers.stream().min(Comparator.comparing(t -> t)).orElse(0);
                int max1 = numbers.stream().max(Comparator.comparing(t -> t)).orElse(0);
                int max = Collections.max(numbers);
                int min = Collections.min(numbers);

                try (FileOutputStream outputStream = new FileOutputStream("D:\\numbers_" + min + "-" + max + ".txt");
                     OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))
                {
                    HttpClient httpClient = HttpClients.custom().setConnectionManager(new PoolingHttpClientConnectionManager(RegistryBuilder.<ConnectionSocketFactory>create()
                            .register("http", PlainConnectionSocketFactory.getSocketFactory())
                            .register("https", new SSLConnectionSocketFactory(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build(), new NoopHostnameVerifier()))
                            .build())).build();
                    String url = "https://cx.shouji.360.cn/phonearea.php?number=%s";

                    for (String phone : phones)
                    {
                        String _url = String.format(url, phone);
                        HttpGet httpGet = new HttpGet(_url);
                        try
                        {
                            String result = httpClient.execute(new HttpGet(_url), response -> EntityUtils.toString(response.getEntity()));
                            JSONObject json1 = JSON.parseObject(result);
                            if (json1 == null)
                            {
                                continue;
                            }
                            JSONObject data = json1.getJSONObject("data");
                            if (data == null)
                            {
                                continue;
                            }
                            System.out.println(phone + " " + data.get("province") + "  " + data.get("city") + "  " + data.get("sp"));
                            Thread.sleep(1000 * new Random().nextInt(5 - 3 + 1) + 3);

                            writer.write(phone + " " + data.get("province") + "  " + data.get("city") + "  " + data.get("sp"));
                            writer.write("\n");
                            writer.flush();

                        }
                        catch (Exception e)
                        {
                            writer.write(phone + " " + e.getMessage() + "[ERROR]");
                            writer.write("\n");
                            writer.flush();
                        }
                        finally
                        {
                            httpGet.releaseConnection();
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }, service);
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    @Test
    public void download() throws Exception
    {
        HttpClientUtil httpClientUtil = new HttpClientUtil(new HttpClientUtil.Config());

        Map<String, String> headers = new HashMap<>();

        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        headers.put("Sec-Ch-Ua", "\"Not.A/Brand\";v=\"8\", \"Chromium\";v=\"114\", \"Google Chrome\";v=\"114\"");
        headers.put("Sec-Ch-Ua-Platform:", "\"Windows\"");
        headers.put("Sec-Fetch-Mode", "navigate");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Sec-Ch-Ua-Mobile", "?0");
        headers.put("Sec-Fetch-Dest", "document");
        headers.put("Sec-Fetch-User", "?1");
        headers.put("Upgrade-Insecure-Requests", "1");
        httpClientUtil.get("https://www.yunxikj.com/uf2108/f1d02ad9837b1c5624c1eb63f96c36f7.html",
                headers, new HashMap<>(), new ResponseHandler<byte[]>()
                {
                    @Override
                    public byte[] handleResponse(HttpResponse httpResponse) throws
                            ClientProtocolException, IOException
                    {
                        try (InputStream input = httpResponse.getEntity().getContent();
                             FileOutputStream out = new FileOutputStream("D:\\1234.rar"))
                        {
                            IOUtils.copy(input, out);
                        }
                        return new byte[0];
                    }
                }, 0);
    }

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
        Object infos = httpClientUtil.postFormData(url,
                new HashMap<>(), body, new HashMap<>(),
                new HttpClientUtil.JsonResponseHandler<>(new TypeReference<Object>(){}));
        System.out.println(infos);

        body.put("id", "2");
        body.put("name", "2");
        infos = httpClientUtil.postFormUrlencoded(url,
                new HashMap<>(), body,
                new HttpClientUtil.JsonResponseHandler<>(new TypeReference<Object>(){}));
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
        HttpClientUtil.ProxyInfo proxyInfo = new HttpClientUtil.ProxyInfo(true, "127.0.0.1", 8089, "http");
        HttpFormDataUtil.BaseResponse response = HttpFormDataUtil.postFormData(url,
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
        Map<String, Object> response = httpClientUtil.postFormData(url,
                headers,
                nameWithContent,
                Collections.singletonMap("file", "D:\\env.txt"),
                new HttpClientUtil.JsonResponseHandler<>(new TypeReference<Map<String, Object>>(){}));
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
