package com.yk.core;

import java.io.Serializable;

/**
 * 请求发送的文件信息
 *
 * @author yangk
 * @version 1.0
 * @since 2021/5/21 13:33
 */
public class FileInfo implements Serializable
{
    private static final long serialVersionUID = -4748864545916409513L;

    private String name;

    private String path;

    private String jobId;

    private long size;
    private long modifyDate;
    private long createDate;

    public FileInfo(String name, String path)
    {
        this.name = name;
        this.path = path;
    }

    public FileInfo(String name, String path, String jobId)
    {
        this.name = name;
        this.path = path;
        this.jobId = jobId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getJobId()
    {
        return jobId;
    }

    public void setJobId(String jobId)
    {
        this.jobId = jobId;
    }

    public long getSize()
    {
        return size;
    }

    public void setSize(long size)
    {
        this.size = size;
    }

    public long getModifyDate()
    {
        return modifyDate;
    }

    public void setModifyDate(long modifyDate)
    {
        this.modifyDate = modifyDate;
    }

    public long getCreateDate()
    {
        return createDate;
    }

    public void setCreateDate(long createDate)
    {
        this.createDate = createDate;
    }

    @Override
    public String toString()
    {
        return "FileInfo{" +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
