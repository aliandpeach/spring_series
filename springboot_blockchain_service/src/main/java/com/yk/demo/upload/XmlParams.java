package com.yk.demo.upload;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * XmlParams
 *
 * @author yangk
 * @version 1.0
 * @since 2021/4/28 16:37
 */
@Data
@XmlRootElement(name = "XmlParams")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlParams
{
    private String id;
    
    private String name;
    
    @XmlElement(name = "XmlInner")
    private XmlInner xmlInner;
}
