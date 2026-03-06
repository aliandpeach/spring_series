package com.yk.test.ws.sm.impl;

import com.yk.test.ws.sm.WSSmCommUpper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@Service("wsSmCommUpper")
// 在wsdl信息文件中 definitions-targetNamespace 必须和 xs:schema-namespace名称保持一致, 使用cxf发布需要在接口和实现类的注释中定义相同的targetNamespace（不必非要写成类限定名的形式）
@WebService(targetNamespace = "http://wssmcommupper/")
public class WSSmCommUpperImpl implements WSSmCommUpper
{
    private static final Logger logger = LoggerFactory.getLogger(WSSmCommUpperImpl.class);

    @Override
    public int reportConfig(String deviceId, String oprCode, String configXml)
    {
        return 11;
    }

    @Override
    public int reportPolicy(String deviceId, String oprCode, String policyXml)
    {
        return 11;
    }

    @Override
    public int reportHealthView(String deviceId, String oprCode, String viewXml)
    {
        System.out.println("reportHealthView: " + deviceId + ", " + oprCode + ", " + viewXml);
        return 11;
    }

    @Override
    public int reportView(String deviceId, String oprCode, String viewXml)
    {
        System.out.println("reportView: " + deviceId + ", " + oprCode + ", " + viewXml);
        return 11;
    }

    @Override
    public int reportEvent(String deviceId, String oprCode, String eventXml)
    {
        System.out.println("reportEvent: " + deviceId + ", " + oprCode + ", " + eventXml);
        return 11;
    }

    @Override
    public int reportLog(String deviceId, String oprCode, String logXml)
    {
        System.out.println("reportLog: " + deviceId + ", " + oprCode + ", " + logXml);
        return 11;
    }
}
