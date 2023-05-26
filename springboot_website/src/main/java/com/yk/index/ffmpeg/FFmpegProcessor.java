package com.yk.index.ffmpeg;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FFmpegLogCallback;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * javacv
 */
@Service
public class FFmpegProcessor
{
    private static final Logger logger = LoggerFactory.getLogger(FFmpegProcessor.class);

    public static void writeKeyInfo(String keyInfoPath, String decrypt, String encrypt, String IV) throws IOException
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(keyInfoPath));)
        {
            writer.write(decrypt);
            writer.newLine();
            writer.write(encrypt);
            writer.newLine();
            if (StringUtils.isNotBlank(IV))
            {
                writer.write(IV);
            }
            writer.flush();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void convertMediaToM3u8ByHttp(InputStream inputStream,
                                         String toFilePath) throws IOException
    {
        avutil.av_log_set_level(avutil.AV_LOG_INFO);
        FFmpegLogCallback.set();

        String fileName = UUID.randomUUID().toString().replaceAll("-", "");
        File m3u8TmpFile = new File(toFilePath, fileName + ".m3u8");

        String prefixName = toFilePath + File.separator + fileName;
        //生成加密key
        String secureFileName = prefixName + ".key";
        byte[] secureRandom = new byte[16];
        FileUtil.writeBytes(secureRandom, secureFileName);

        String toHex = Convert.toHex(secureRandom);
        String keyInfoPath = toFilePath + File.separator + "key.keyinfo";
        //写入加密文件
        writeKeyInfo(keyInfoPath, fileName + ".key", secureFileName, toHex);

        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputStream);
             FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(m3u8TmpFile, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels());)
        {
            avutil.av_log_set_level(avutil.AV_LOG_INFO);
            FFmpegLogCallback.set();

            //grabber.setAudioChannels(1);
            grabber.start();

            //格式方式
            recorder.setFormat("hls");
            //关于hls_wrap的说明，hls_wrap表示重复覆盖之前ts切片，这是一个过时配置，ffmpeg官方推荐使用hls_list_size 和hls_flags delete_segments代替hls_wrap
            //设置单个ts切片的时间长度（以秒为单位）。默认值为2秒
            recorder.setOption("hls_time", "10");
            //不根据gop间隔进行切片,强制使用hls_time时间进行切割ts分片
            recorder.setOption("hls_flags", "split_by_time");

            // 设置播放列表条目的最大数量。如果设置为0，则列表文件将包含所有片段，默认值为5
            // 当切片的时间不受控制时，切片数量太小，就会有卡顿的现象
            recorder.setOption("hls_list_size", "0");
            // 自动删除切片，如果切片数量大于hls_list_size的数量，则会开始自动删除之前的ts切片，只保留hls_list_size个数量的切片
            recorder.setOption("hls_flags", "delete_segments");
            // ts切片自动删除阈值，默认值为1，表示早于hls_list_size+1的切片将被删除
            recorder.setOption("hls_delete_threshold", "1");
            // hls的切片类型：
            // 'mpegts'：以MPEG-2传输流格式输出ts切片文件，可以与所有HLS版本兼容。
            // 'fmp4':以Fragmented MP4(简称：fmp4)格式输出切片文件，类似于MPEG-DASH，fmp4文件可用于HLS version 7和更高版本。
            recorder.setOption("hls_segment_type", "mpegts");
            //指定ts切片生成名称规则，按数字序号生成切片,例如'file%03d.ts'，就会生成file000.ts，file001.ts，file002.ts等切片文件
            recorder.setOption("hls_segment_filename", toFilePath + File.separator + fileName + "-%5d.ts");
            //加密
            recorder.setOption("hls_key_info_file", keyInfoPath);
            // 设置第一个切片的编号
//          recorder.setOption("start_number", String.valueOf(tsCont));
//          recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);

            // 转码
            logger.info("{} | 启动Hls转码录制器……", toFilePath);
            //      设置零延迟
            //recorder.setVideoOption("tune", "zerolatency");
            recorder.setVideoOption("tune", "fastdecode");
            // 快速
            recorder.setVideoOption("preset", "ultrafast");
//          recorder.setVideoOption("crf", "26");
            recorder.setVideoOption("threads", "12");
            recorder.setVideoOption("vsync", "2");
            recorder.setFrameRate(grabber.getFrameRate());// 设置帧率
//          recorder.setGopSize(25);// 设置gop,与帧率相同，相当于间隔1秒chan's一个关键帧
//		    recorder.setVideoBitrate(100 * 1000);// 码率500kb/s
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
//          recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
            recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);


            // 如果想截取规定时间段视频
            recorder.start();
            Frame frame;
            while ((frame = grabber.grabImage()) != null)
            {
                try
                {
                    recorder.record(frame);
                }
                catch (FrameRecorder.Exception e)
                {
                    logger.error("转码异常：{}", e.getMessage());
                }
            }
//            recorder.start(grabber.getFormatContext());
            /*AVPacket packet;
            while ((packet = grabber.grabPacket()) != null)
            {
                try
                {
                    recorder.recordPacket(packet);
                }
                catch (FrameRecorder.Exception e)
                {
                    logger.error("转码异常：{}", e.getMessage());
                }
            }*/
            recorder.setTimestamp(grabber.getTimestamp());
            recorder.stop();
            recorder.release();
            grabber.stop();
            grabber.release();
            logger.info("转码m3u8：{}", m3u8TmpFile.getAbsolutePath());
        }
    }

    /**
     * 这个方法的url地址都必须是一样的类型 同为post
     */
    public void convertMediaToM3u8ByHttp(InputStream inputStream,
                                         String m3u8Url,
                                         String infoUrl,
                                         String hls_segment_filename) throws IOException
    {

        avutil.av_log_set_level(avutil.AV_LOG_INFO);
        FFmpegLogCallback.set();

        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputStream);
             FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(m3u8Url, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels());)
        {
            grabber.start();

            recorder.setFormat("hls");
            recorder.setOption("hls_time", "5");
            recorder.setOption("hls_list_size", "0");
            recorder.setOption("hls_flags", "delete_segments");
            recorder.setOption("hls_delete_threshold", "1");
            recorder.setOption("hls_segment_type", "mpegts");
            recorder.setOption("hls_segment_filename", hls_segment_filename);
            recorder.setOption("hls_key_info_file", infoUrl);
            recorder.setOption("method", "POST");

            recorder.setFrameRate(25);
            recorder.setGopSize(2 * 25);
            recorder.setVideoQuality(1.0);
            recorder.setVideoBitrate(10 * 1024);
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
            recorder.start();

            Frame frame;
            while ((frame = grabber.grabImage()) != null)
            {
                recorder.record(frame);
            }
            recorder.setTimestamp(grabber.getTimestamp());
        }
    }
}
