package com.yk.demo.upload;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * fdf
 *
 * @author yangk
 * @version 1.0
 * @since 2021/4/28 16:47
 */

@Data
@XmlRootElement(name = "XmlInner")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlInner
{
    private String innerName;
}