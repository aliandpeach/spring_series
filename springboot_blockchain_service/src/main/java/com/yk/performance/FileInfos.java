package com.yk.performance;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * FileInfoParam
 */
@Data
@XmlRootElement(name = "files")
@XmlAccessorType(XmlAccessType.FIELD)
public class FileInfos
{
    @Valid
    @XmlElement(name = "file")
    @NotNull
    private List<FileInfoParam> fileInfoParamList;
}
