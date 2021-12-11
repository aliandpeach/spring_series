package xml;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/08/25 11:35:55
 */
public class Dom4jUtil
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Dom4jUtil.class);

    public static Document getDocumentFile(String xmlFilePath)
    {
        if (!new File(xmlFilePath).exists())
        {
            return null;
        }
        SAXReader reader = new SAXReader();
        try (InputStream input = new FileInputStream(xmlFilePath))
        {
            return reader.read(input);
        }
        catch (Exception e)
        {
            LOGGER.error("init read xml file error", e);
        }
        return null;
    }

    public static Document getDocumentStream(InputStream input)
    {
        if (null == input)
        {
            return null;
        }
        SAXReader reader = new SAXReader();
        try
        {
            return reader.read(input);
        }
        catch (Exception e)
        {
            LOGGER.error("init read xml file error", e);
        }
        return null;
    }

    public static Document getDocumentContent(String xmlContent)
    {
        SAXReader reader = new SAXReader();
        try (ByteArrayInputStream input = new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8)))
        {
            return reader.read(input);
        }
        catch (Exception e)
        {
            LOGGER.error("init read xml file error", e);
        }
        return null;
    }

    public static void writeToXml(Document document, String xml) throws Exception
    {
        try (FileOutputStream outStream = new FileOutputStream(new File(xml));
             OutputStreamWriter out = new OutputStreamWriter(outStream, StandardCharsets.UTF_8))
        {
            // 排版缩进的格式
            OutputFormat format = OutputFormat.createPrettyPrint();
            // 设置编码
            format.setEncoding("UTF-8");
            // 创建XMLWriter对象,指定了写出文件及编码格式
            XMLWriter writer = new XMLWriter(out);
            writer.write(document);
            writer.flush();
            writer.close();
        }
    }
}
