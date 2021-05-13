package com.yk.performance;

import com.google.common.base.Objects;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * FileInfoParam
 */
@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class FileInfoParam
{
    @NotNull
    @NotEmpty
    private String id;

    @NotNull
    @NotEmpty
    private String name;

    private long size;

    private String modifyDate;

    private String md5;

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileInfoParam that = (FileInfoParam) o;
        return Objects.equal(id, that.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(id);
    }
}
