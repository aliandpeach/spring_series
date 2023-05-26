package play;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameUtils;
import org.bytedeco.opencv.opencv_core.IplImage;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.Map;

public class FrameGrabberTest
{
    public static void main(String[] args) throws Exception, InterruptedException
    {
        FrameGrabber grabber = new FFmpegFrameGrabber("https://hd-auth.skylinewebcams.com/live.m3u8?a=roav0li8uitf88ojqqr4n1g2l6");
        // grabber.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
        // 设置缓存大小，提高画质、减少卡顿花屏
//        grabber.setOption("buffer_size", "1024000");

        grabber.start();   //开始获取摄像头数据
        CanvasFrame canvas = new CanvasFrame("摄像头");//新建一个窗口
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas.setAlwaysOnTop(true);

        while(true)
        {
            if(!canvas.isDisplayable())
            {//窗口是否关闭
                grabber.stop();//停止抓取
                System.exit(2);//退出
            }
            Frame grab = grabber.grab();
            Map<String, String> audioMetadata = grabber.getAudioMetadata();
            IplImage iplImage = Java2DFrameUtils.toIplImage(grab);
            BufferedImage buffImg=Java2DFrameUtils.toBufferedImage(iplImage);
            Graphics2D graphics = buffImg.createGraphics();
            graphics.setColor(Color.BLUE);
            graphics.setFont(new Font("微软雅黑", Font.BOLD, 40));
            graphics.drawString(LocalDateTime.now().toString(),(iplImage.width()/2)-300,iplImage.height()-50);
            graphics.dispose();
            Frame newFrame = Java2DFrameUtils.toFrame(buffImg);
            canvas.showImage(newFrame);//获取摄像头图像并放到窗口上显示， 这里的Frame frame=grabber.grab(); frame是一帧视频图像
            Thread.sleep(50);//50毫秒刷新一次图像
        }
    }
}
