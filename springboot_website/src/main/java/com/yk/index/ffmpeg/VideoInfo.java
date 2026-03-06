package com.yk.index.ffmpeg;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoInfo
{
    /**
     * 总帧数
     **/
    private int lengthInFrames;

    /**
     * 帧率
     **/
    private double frameRate;

    /**
     * 时长
     **/
    private double duration;

    /**
     * 视频编码
     */
    private String videoCode;
    /**
     * 音频编码
     */
    private String audioCode;

    private int width;
    private int height;
    private int audioChannel;
    private String md5;
    /**
     * 音频采样率
     */
    private int sampleRate;
}