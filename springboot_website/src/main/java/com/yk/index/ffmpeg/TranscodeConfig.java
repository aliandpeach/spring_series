package com.yk.index.ffmpeg;

import lombok.Data;

@Data
public class TranscodeConfig
{
    private String poster;    // 截取封面的时间			HH:mm:ss.[SSS]
    private String tsSeconds; // ts分片大小, 单位是秒
    private String cutStart;  // 视频裁剪，开始时间		HH:mm:ss.[SSS]
    private String cutEnd;    // 视频裁剪，结束时间		HH:mm:ss.[SSS]

    @Override
    public String toString()
    {
        return "TranscodeConfig [poster=" + poster + ", tsSeconds=" + tsSeconds + ", cutStart=" + cutStart + ", cutEnd="
                + cutEnd + "]";
    }
}
