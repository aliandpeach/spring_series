package hls;

import com.yk.httprequest.HttpClientUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HlsDownloadTest
{
    public static void main(String[] args) throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException
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
        byte[] m3u = httpClientUtil.get("https://m3u.haiwaikan.com/xm3u8/7a4acbd934a667dea20989fab0be4760d93d6b5ce00236958323dc1b326ab9c79921f11e97d0da21.m3u8",
                headers, new HashMap<>(), new ResponseHandler<byte[]>()
                {
                    @Override
                    public byte[] handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException
                    {
                        try (InputStream input = httpResponse.getEntity().getContent())
                        {
                            return IOUtils.toByteArray(input);
                        }
                    }
                }, 0);
        String str = IOUtils.toString(m3u, StandardCharsets.UTF_8.toString());
        System.out.println(str);
    }
}