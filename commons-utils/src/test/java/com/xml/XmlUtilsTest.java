package com.xml;

import xml.XmlUtils;

import java.nio.charset.StandardCharsets;

public class XmlUtilsTest
{
    public static void main(String agrs[]) throws Exception
    {
        PolicyPack policyPack = new PolicyPack();
        PolicyXml policy = new PolicyXml();
        policy.setCode("ServiceObject_Policy");
        policy.setVersion("1.01");
        policy.setDescription("网络监控策略");
        policyPack.setPolicy(policy);

        SpPlcDiscoveryTasksXml xml = new SpPlcDiscoveryTasksXml();
        xml.setId("123");
        policy.setContent(com.alibaba.fastjson2.JSON.toJSONString(xml));

        String policyXml = XmlUtils.toXml(policyPack, PolicyPack.class, StandardCharsets.UTF_8.name());
        System.out.println(policyXml);

        PolicyPack _pack = XmlUtils.convertToBean(PolicyPack.class, policyXml);
        System.out.println(_pack.getPolicy().getContent());
        SpPlcDiscoveryTasksXml _xml = com.alibaba.fastjson2.JSON.parseObject(_pack.getPolicy().getContent(), SpPlcDiscoveryTasksXml.class);
        System.out.println(_xml);

        PolicyPack _pack2 = new XmlUtils<PolicyPack>().convertToBean(PolicyPack.class, policyXml);
        System.out.println(_pack2.getPolicy().getContent());
        SpPlcDiscoveryTasksXml _xml2 = com.alibaba.fastjson2.JSON.parseObject(_pack2.getPolicy().getContent(), SpPlcDiscoveryTasksXml.class);
        System.out.println(_xml2);
    }
}
