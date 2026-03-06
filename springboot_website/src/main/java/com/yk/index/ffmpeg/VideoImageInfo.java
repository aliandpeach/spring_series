package com.yk.index.ffmpeg;

import lombok.Data;

@Data
public class VideoImageInfo
{
    /**
     * 服务器保存的文件地址
     */
    private String filePath;

    /**
     * 文件原始名
     */
    private String fileOriginalName;

    /**
     * 新的文件名
     */
    private String fileNewName;

    /**
     * 文件大小
     */
    private long size;

    /**
     * 文件类型 【0 视频， 1 图片， 2 其它附件 3 头像数据， 4 顶部大图数据】
     */
    private int type;

    /**
     * 后缀名
     */
    private String suffixName;

    /**
     * 视频长度
     */
    private double duration;

    private int height;

    private int width;

    /**
     * 像素数
     */
    private long pixelsNumber;


    /**
     * 帧率
     */
    private double frameRate;


    /**
     * 详细信息 JSON
     */
    private String info;
}
