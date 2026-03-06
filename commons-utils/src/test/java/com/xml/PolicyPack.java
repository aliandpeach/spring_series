package com.xml;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "PolicyPack")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class PolicyPack// implements Serializable
{
    // private static final long serialVersionUID = -2853121908462390823L;
    @XmlElement(name = "Policy")
    private PolicyXml policy;
}
