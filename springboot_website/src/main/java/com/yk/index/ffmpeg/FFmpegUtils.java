package com.yk.index.ffmpeg;

import com.google.gson.Gson;
import org.apache.commons.codec.binary.Hex;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.crypto.KeyGenerator;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class FFmpegUtils
{
    private static final Logger logger = LoggerFactory.getLogger(FFmpegUtils.class);

    private final static String SUFFIX = ".jpg";

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * 获取视频文件信息
     */
    public static VideoInfo getVideoInfo(File file)
    {
        VideoInfo videoInfo = new VideoInfo();
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(file))
        {
            grabber.start();

            videoInfo.setLengthInFrames(grabber.getLengthInVideoFrames());

            videoInfo.setFrameRate(grabber.getVideoFrameRate());

            videoInfo.setDuration(grabber.getLengthInTime() / 1000000.00);

            videoInfo.setWidth(grabber.getImageWidth());

            videoInfo.setHeight(grabber.getImageHeight());

            videoInfo.setAudioChannel(grabber.getAudioChannels());

            videoInfo.setVideoCode(grabber.getVideoCodecName());

            videoInfo.setAudioCode(grabber.getAudioCodecName());
            // String md5 = MD5Util.getMD5ByInputStream(new FileInputStream(file));

            videoInfo.setSampleRate(grabber.getSampleRate());
            return videoInfo;
        }
        catch (Exception e)
        {
            logger.error("javacv obtain video information error", e);
            return null;
        }
    }

    /**
     * 随机获取视频截图
     *
     * @param videFile 视频文件
     * @param count    输出截图数量
     * @return 截图列表
     */
    public static List<VideoImageInfo> randomGrabberFFmpegImage(File videFile, int count, String path)
    {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videFile))
        {
            List<VideoImageInfo> images = new ArrayList<>(count);
            grabber.start();
            // 获取视频总帧数
            // int lengthInVideoFrames = grabber.getLengthInVideoFrames();
            // 获取视频时长， / 1000000 将单位转换为秒
            long delayedTime = grabber.getLengthInTime() / 1000000;

            Random random = SecureRandom.getInstanceStrong();
            int[] timeList = new int[count];
            for (int i = 0; i < count; i++)
            {
                timeList[i] = random.nextInt((int) delayedTime - 1) + 1;
            }
            // 让截图按时间线排列
            Arrays.sort(timeList);
            for (int i : timeList)
            {
                // 跳转到响应时间
                grabber.setTimestamp(i * 1000000L);
                Frame f = grabber.grabImage();
                Java2DFrameConverter converter = new Java2DFrameConverter();
                BufferedImage bi = converter.getBufferedImage(f);
                String imageName = UUID.randomUUID().toString().replace("-", "") + SUFFIX;
                File out = Paths.get(path, imageName).toFile();
                ImageIO.write(bi, "jpg", out);

                VideoImageInfo videoImageInfo = new VideoImageInfo();
                videoImageInfo.setFilePath(path + File.separator + imageName);
                videoImageInfo.setFileNewName(imageName);
                videoImageInfo.setSize(f.image.length);
                videoImageInfo.setFileOriginalName(videFile.getName());
                videoImageInfo.setType(5);
                videoImageInfo.setSuffixName(SUFFIX);

                images.add(videoImageInfo);
            }
            return images;
        }
        catch (Exception e)
        {
            logger.error("javacv obtain video images error", e);
            return null;
        }
    }

    /**
     * @param inputFilePath  inputFilePath
     * @param outputFilePath outputFilePath
     * @param baseUrl        baseUrl
     * @param filename       filename
     * @param keyInfo        格式为
     *                       /upload/key/xxx/encrypt.key        - uri
     *                       F:\Movies\encrypt.key              - key file path, 生成 openssl random 16 > encrypt.key
     *                       a9a9b1d3c819990ef4f7284f9ffd1320   iv
     */
    public static void convertMediaToM3u8(String inputFilePath, String outputFilePath, String baseUrl, String filename, String keyInfo) throws IOException
    {
        avutil.av_log_set_level(avutil.AV_LOG_INFO);
        FFmpegFrameGrabber grabber = null;
        FFmpegFrameRecorder recorder = null;
        try
        {
            grabber = new FFmpegFrameGrabber(inputFilePath);
            grabber.start();

            recorder = new FFmpegFrameRecorder(outputFilePath,
                    grabber.getImageWidth(),
                    grabber.getImageHeight(),
                    grabber.getAudioChannels());

            recorder.setFormat("hls");
            recorder.setOption("hls_time", "5");
            recorder.setOption("hls_list_size", "0");
            recorder.setOption("hls_flags", "delete_segments");
            recorder.setOption("hls_delete_threshold", "1");
            recorder.setOption("hls_segment_type", "mpegts");
            if (null != keyInfo && keyInfo.trim().length() > 0)
            {
                recorder.setOption("hls_key_info_file", keyInfo);
            }
            recorder.setOption("hls_segment_filename", filename + "_%d.ts");
            recorder.setOption("hls_base_url", baseUrl);

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
        catch (Exception e)
        {
            logger.error("javacv convert video to m3u8 error", e);
        }
        finally
        {
            try
            {
                if (null != recorder)
                {
                    recorder.stop();
                    recorder.release();
                }
            }
            catch (Exception e)
            {
                logger.error("javacv close recorder error", e);
            }
            try
            {
                if (null != grabber)
                {
                    grabber.stop();
                    grabber.release();
                }
            }
            catch (Exception e)
            {
                logger.error("javacv close grabber error", e);
            }
        }
    }

    /*=======================================以上使用javacv===============================================*/

    /**
     * 生成随机16个字节的AESKEY
     */
    private static byte[] genAesKey()
    {
        try
        {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            return keyGenerator.generateKey().getEncoded();
        }
        catch (NoSuchAlgorithmException e)
        {
            return null;
        }
    }

    /**
     * 在指定的目录下生成key_info, key文件，返回key_info文件
     *
     * @param folder
     */
    private static Path genKeyInfo(String folder) throws IOException
    {
        byte[] aesKey = genAesKey();
        if (null == aesKey)
        {
            return null;
        }
        // AES 向量
        String iv = Hex.encodeHexString(Objects.requireNonNull(genAesKey()));

        // key 文件写入
        Path keyFile = Paths.get(folder, "key");
        Files.write(keyFile, aesKey, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        // key_info 文件写入

        Path keyInfo = Paths.get(folder, "key_info");

        String stringBuilder = "key" + LINE_SEPARATOR +
                keyFile.toString() + LINE_SEPARATOR +
                iv;
        Files.write(keyInfo, stringBuilder.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        return keyInfo;
    }

    /**
     * 指定的目录下生成 master index.m3u8 文件
     *
     * @param file      master m3u8文件
     * @param indexPath 访问子index.m3u8的路径
     * @param bandWidth 流码率
     * @throws IOException
     */
    private static void genIndex(String file, String indexPath, String bandWidth) throws IOException
    {
        String stringBuilder = "#EXTM3U" + LINE_SEPARATOR +
                "#EXT-X-STREAM-INF:BANDWIDTH=" + bandWidth + LINE_SEPARATOR +  // 码率
                indexPath;
        Files.write(Paths.get(file), stringBuilder.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    /**
     * 转码视频为m3u8
     *
     * @param source     源视频
     * @param destFolder 目标文件夹
     * @param config     配置信息
     */
    public static void transcodeToM3u8(String source, String destFolder, TranscodeConfig config) throws IOException, InterruptedException
    {

        // 判断源视频是否存在
        if (!Files.exists(Paths.get(source)))
        {
            throw new IllegalArgumentException("文件不存在：" + source);
        }

        // 创建工作目录
        Path workDir = Paths.get(destFolder, "ts");
        Files.createDirectories(workDir);

        // 在工作目录生成KeyInfo文件
        Path keyInfo = genKeyInfo(workDir.toString());

        // 构建命令
        List<String> commands = new ArrayList<>();
        commands.add("ffmpeg");
        commands.add("-i");
        commands.add(source);                    // 源文件
        commands.add("-c:v");
        commands.add("libx264");                // 视频编码为H264
        commands.add("-c:a");
        commands.add("copy");                    // 音频直接copy
        commands.add("-hls_key_info_file");
        commands.add(keyInfo.toString());        // 指定密钥文件路径
        commands.add("-hls_time");
        commands.add(config.getTsSeconds());    // ts切片大小
        commands.add("-hls_playlist_type");
        commands.add("vod");                    // 点播模式
        commands.add("-hls_segment_filename");
        commands.add("%06d.ts");                // ts切片文件名称

        if (StringUtils.hasText(config.getCutStart()))
        {
            commands.add("-ss");
            commands.add(config.getCutStart());    // 开始时间
        }
        if (StringUtils.hasText(config.getCutEnd()))
        {
            commands.add("-to");
            commands.add(config.getCutEnd());        // 结束时间
        }
        commands.add("index.m3u8");                                                        // 生成m3u8文件

        // 构建进程
        Process process = new ProcessBuilder()
                .command(commands)
                .directory(workDir.toFile())
                .start();

        // 读取进程标准输出
        new Thread(() ->
        {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream())))
            {
                String line = null;
                while ((line = bufferedReader.readLine()) != null)
                {
                    logger.info(line);
                }
            }
            catch (IOException e)
            {
            }
        }).start();

        // 读取进程异常输出
        new Thread(() ->
        {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream())))
            {
                String line = null;
                while ((line = bufferedReader.readLine()) != null)
                {
                    logger.info(line);
                }
            }
            catch (IOException e)
            {
            }
        }).start();


        // 阻塞直到任务结束
        if (process.waitFor() != 0)
        {
            throw new RuntimeException("视频切片异常");
        }

        // 切出封面
        if (!screenShots(source, String.join(File.separator, destFolder, "poster.jpg"), config.getPoster()))
        {
            throw new RuntimeException("封面截取异常");
        }

        // 获取视频信息
        MediaInfo mediaInfo = getMediaInfo(source);
        if (mediaInfo == null)
        {
            throw new RuntimeException("获取媒体信息异常");
        }

        // 生成index.m3u8文件
        genIndex(String.join(File.separator, destFolder, "index.m3u8"), "ts/index.m3u8", mediaInfo.getFormat().getBitRate());

        // 删除keyInfo文件
        Files.delete(keyInfo);
    }

    /**
     * 获取视频文件的媒体信息
     *
     * @param source source
     */
    public static MediaInfo getMediaInfo(String source) throws IOException, InterruptedException
    {
        List<String> commands = new ArrayList<>();
        commands.add("ffprobe");
        commands.add("-i");
        commands.add(source);
        commands.add("-show_format");
        commands.add("-show_streams");
        commands.add("-print_format");
        commands.add("json");

        Process process = new ProcessBuilder(commands)
                .start();

        MediaInfo mediaInfo = null;

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream())))
        {
            mediaInfo = new Gson().fromJson(bufferedReader, MediaInfo.class);
        }
        catch (IOException e)
        {
        }

        if (process.waitFor() != 0)
        {
            return null;
        }
        return mediaInfo;
    }

    /**
     * 截取视频的指定时间帧，生成图片文件
     *
     * @param source 源文件
     * @param file   图片文件
     * @param time   截图时间 HH:mm:ss.[SSS]
     * @throws IOException
     * @throws InterruptedException
     */
    public static boolean screenShots(String source, String file, String time) throws IOException, InterruptedException
    {

        List<String> commands = new ArrayList<>();
        commands.add("ffmpeg");
        commands.add("-i");
        commands.add(source);
        commands.add("-ss");
        commands.add(time);
        commands.add("-y");
        commands.add("-q:v");
        commands.add("1");
        commands.add("-frames:v");
        commands.add("1");
        commands.add("-f");
        ;
        commands.add("image2");
        commands.add(file);

        Process process = new ProcessBuilder(commands).start();

        new Thread(() ->
        {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream())))
            {
                String line = null;
                while ((line = bufferedReader.readLine()) != null)
                {
                    logger.info(line);
                }
            }
            catch (IOException e)
            {
            }
        }).start();

        // 读取进程异常输出
        new Thread(() ->
        {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream())))
            {
                String line = null;
                while ((line = bufferedReader.readLine()) != null)
                {
                    logger.error(line);
                }
            }
            catch (IOException e)
            {
            }
        }).start();

        return process.waitFor() == 0;
    }
}
