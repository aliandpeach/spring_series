package com.xml;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import xml.Dom4jUtil;

import java.io.InputStream;
import java.util.List;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/08/26 14:21:50
 */
public class Dom4jUtilTest
{
    public static void main(String[] args) throws Exception
    {
        InputStream input = Dom4jUtilTest.class.getClassLoader().getResourceAsStream("com/xml/work.xml");

        System.out.println(Dom4jUtilTest.class.getResource("/").getPath());
        InputStream input2 = Dom4jUtilTest.class.getResourceAsStream("/com/xml/work.xml");

        System.out.println(Dom4jUtilTest.class.getResource("").getPath());
        InputStream input3 = Dom4jUtilTest.class.getResourceAsStream("work.xml");

        Document document = Dom4jUtil.getDocumentStream(input);
        if (null == document)
        {
            document = DocumentHelper.createDocument();
            Element element = document.addElement("local");
        }
        assert document != null;
        List<Node> node = document.selectNodes("//work/id");
        List<Node> node2 = document.selectNodes("//group"); // 3个不同节点下的group

        Document doc = node.get(0).getDocument();

        Element root = document.getRootElement();
        root.addElement("local");

        Dom4jUtil.writeToXml(document, "C:\\Users\\yangkai\\Desktop\\1.xml");
        System.out.println(node);
    }
}
