package xml;

import org.apache.commons.lang3.Validate;
import org.springframework.util.StringUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class XmlUtils<T> {

    @SuppressWarnings("rawtypes")
    private static ConcurrentMap<Class, JAXBContext> jaxbContexts = new ConcurrentHashMap<Class, JAXBContext>();

    public static <T> T convertToBean(Class<T> clazz, String xmlStr) throws JAXBException {
        StringReader stringReader = new StringReader(xmlStr);
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (T) unmarshaller.unmarshal(stringReader);
    }

    public T convertToBean(T t, String xmlStr) throws JAXBException
    {
        StringReader stringReader = new StringReader(xmlStr);
        JAXBContext jaxbContext = JAXBContext.newInstance(t.getClass());
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (T) unmarshaller.unmarshal(stringReader);
    }


    /**
     * Java Object->Xml with encoding.
     */
    @SuppressWarnings("rawtypes")
    public static String toXml(Object root, Class clazz, String encoding) throws Exception {
        try {
            StringWriter writer = new StringWriter();
            createMarshaller(clazz, encoding).marshal(root, writer);
            return writer.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建Marshaller并设定encoding(可为null).
     * 线程不安全，需要每次创建或pooling。
     */
    @SuppressWarnings("rawtypes")
    public static Marshaller createMarshaller(Class clazz, String encoding) throws Exception {
        try {
            JAXBContext jaxbContext = getJaxbContext(clazz);

            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            if (!StringUtils.isEmpty(encoding)) {
                marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
            }
            return marshaller;
        } catch (JAXBException e) {
            throw new Exception(e);
        }
    }

    @SuppressWarnings("rawtypes")
    protected static JAXBContext getJaxbContext(Class clazz) {
        Validate.notNull(clazz, "`'clazz' must not be null");
        JAXBContext jaxbContext = jaxbContexts.get(clazz);
        if (jaxbContext == null) {
            try {
                jaxbContext = JAXBContext.newInstance(clazz);
                jaxbContexts.putIfAbsent(clazz, jaxbContext);
            } catch (JAXBException ex) {
                throw new RuntimeException("Could not instantiate JAXBContext for class [" + clazz + "]: "
                        + ex.getMessage(), ex);
            }
        }
        return jaxbContext;
    }


}

