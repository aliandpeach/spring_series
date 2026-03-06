package com.pdf;

import cn.hutool.core.img.Img;
import cn.hutool.core.img.ImgUtil;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPCell;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ItextPdfTest
{
    @Test
    public void export() throws Exception
    {
        String path = "https://ecampus.ynenc.cn:9878/group1/userfiles/stuPic/2022/522427200309287735.jpg";
        URL _url = new URL(path);
        HttpURLConnection _connection = (HttpURLConnection) _url.openConnection();
        _connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        InputStream _inputStream = _connection.getInputStream();
        IOUtils.write(IOUtils.toByteArray(_inputStream), new FileOutputStream("C:\\Users\\yangkai\\Desktop\\522427200309287735_WRITE.jpg"));


        URL url = new URL(path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        InputStream inputStream = connection.getInputStream();
        Image image = Image.getInstance(IOUtils.toByteArray(inputStream));
        PdfPCell imageCell = new PdfPCell(image, true);


        url = new URL(path);
        HttpURLConnection __connection = (HttpURLConnection) url.openConnection();
        __connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        InputStream __inputStream = __connection.getInputStream();
        BufferedImage originalImage = ImageIO.read(__inputStream);
        // ImageIO.write - bug: 有alpha通道的图片都会出现问题，多半是ImageIO.write这个方法出了问题
        // ImageIO.write(originalImage, "jpg", new File("C:\\Users\\yangkai\\Desktop\\522427200309287735_WRITE_0.jpg"));
        Thumbnails.of(originalImage).scale(1f).toFile("C:\\Users\\yangkai\\Desktop\\522427200309287735_originalImage1.jpg");
        Img.from(originalImage).write(new File("C:\\Users\\yangkai\\Desktop\\522427200309287735_originalImage2.jpg"));

        BufferedImage resizedImage = new BufferedImage(100, 200, originalImage.getColorModel().hasAlpha() ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, 100, 200, null);
        g2d.dispose();

        Thumbnails.of(resizedImage).scale(1f).toFile("C:\\Users\\yangkai\\Desktop\\522427200309287735_resizedImage1.jpg");
        Thumbnails.of(resize(originalImage, 100, 200)).scale(1f).toFile("C:\\Users\\yangkai\\Desktop\\522427200309287735_resizedImage2.jpg");

        Img.from(originalImage).scale(100, 200).write(new File("C:\\Users\\yangkai\\Desktop\\522427200309287735_resizedImage3.jpg"));

        java.awt.Image image1 = Img.from(originalImage).scale(100, 200).getImg();
        Image i = Image.getInstance(image1, null);
    }

    public static BufferedImage resize(BufferedImage originalImage, int targetWidth, int targetHeight)
    {
        // 计算缩放比例
        double scaleX = (double) targetWidth / originalImage.getWidth();
        double scaleY = (double) targetHeight / originalImage.getHeight();

        // 使用 AffineTransform 缩放图像
        AffineTransform at = new AffineTransform();
        at.scale(scaleX, scaleY);

        // 创建目标大小的图像
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, originalImage.getType());
        resizedImage = scaleOp.filter(originalImage, resizedImage);

        return resizedImage;
    }
}
