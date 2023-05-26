package hls;

import com.yk.index.ffmpeg.FFmpegProcessor;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;

public class HlsTest
{
    @Test
    public void testFfmpeg() throws IOException
    {
        System.setProperty("catalina.home", "D:\\logs\\");
        new FFmpegProcessor().convertMediaToM3u8ByHttp(new FileInputStream("F:\\Movies\\123.mp4"), "F:\\Movies\\test\\");
    }
}
