package com.xml;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class PolicyXml// implements Serializable
{
    // private static final long serialVersionUID = -75615153134845620L;
    // <Policy code="ServiceObject_Policy" version="1.00" mode="transient" sendMode="ALL" id="" description="服务对象配置策略">
    @XmlAttribute(name = "code")
    private String code;

    @XmlAttribute(name = "version")
    private String version = "1.00";

    @XmlAttribute(name = "mode")
    private String mode = "transient";

    @XmlAttribute(name = "id")
    private String id = "";

    @XmlAttribute(name = "description")
    private String description = "";

    @XmlAttribute(name = "content")
    private String content;

    @XmlAttribute(name = "content1")
    private String content1;
}
