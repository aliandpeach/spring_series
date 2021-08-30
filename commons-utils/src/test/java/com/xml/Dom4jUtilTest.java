package com.xml;

import org.dom4j.Document;
import org.dom4j.Node;
import xml.Dom4jUtil;

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
    public static void main(String[] args)
    {
        Document document = Dom4jUtil.getDocumentFile("work.xml");
        assert document != null;
        List<Node> node = document.selectNodes("//work/id");
        System.out.println(node);
    }
}
