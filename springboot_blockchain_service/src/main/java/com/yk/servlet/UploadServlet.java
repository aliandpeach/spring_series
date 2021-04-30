package com.yk.servlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

/**
 * UploadServlet
 */
@WebServlet("/uploadServlet")
public class UploadServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    
    private static final int BUFFER_SIZE = 100 * 1024;
    
    private Logger logger = LoggerFactory.getLogger(UploadServlet.class);
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);
            if (isMultipart)
            {
                String fileName = "";
                Integer chunk = 0, chunks = 0;
                
                //检查文件目录，不存在则创建
                String relativePath = "/plupload/files/";
                String realPath = request.getServletContext().getRealPath("");
                File folder = new File(realPath + relativePath);
                if (!folder.exists())
                {
                    folder.mkdirs();
                }
                
                DiskFileItemFactory diskFactory = new DiskFileItemFactory();
                // threshold 极限、临界值，即硬盘缓存 1M
                diskFactory.setSizeThreshold(4 * 1024);
                
                ServletFileUpload upload = new ServletFileUpload(diskFactory);
                // 设置允许上传的最大文件大小（单位MB）
                upload.setSizeMax(1024 * 1048576);
                upload.setHeaderEncoding("UTF-8");
                
                try
                {
                    List<FileItem> fileList = upload.parseRequest(request);
                    Iterator<FileItem> it = fileList.iterator();
                    while (it.hasNext())
                    {
                        FileItem item = it.next();
                        String name = item.getFieldName();
                        InputStream input = item.getInputStream();
                        if ("name".equals(name))
                        {
                            fileName = Streams.asString(input);
                            continue;
                        }
                        if ("chunk".equals(name))
                        {
                            chunk = Integer.valueOf(Streams.asString(input));
                            continue;
                        }
                        if ("chunks".equals(name))
                        {
                            chunks = Integer.valueOf(Streams.asString(input));
                            continue;
                        }
                        // 处理上传文件内容
                        if (!item.isFormField())
                        {
                            //目标文件
                            File destFile = new File(folder, fileName);
                            //文件已存在删除旧文件（上传了同名的文件）
                            if (chunk == 0 && destFile.exists())
                            {
                                destFile.delete();
                                destFile = new File(folder, fileName);
                            }
                            //合成文件
                            appendFile(input, destFile);
                            if (chunk == chunks - 1)
                            {
                                logger.info("上传完成");
                            }
                            else
                            {
                                logger.info("还剩[" + (chunks - 1 - chunk) + "]个块文件");
                            }
                        }
                    }
                    response.getOutputStream().write("SUCCESS".getBytes(StandardCharsets.UTF_8));
                }
                catch (FileUploadException ex)
                {
                    logger.warn("上传文件失败：" + ex.getMessage());
                    response.getOutputStream().write("ERROR".getBytes(StandardCharsets.UTF_8));
                    return;
                }
            }
            response.getOutputStream().write("ERROR".getBytes(StandardCharsets.UTF_8));
        }
        catch (IOException e)
        {
            logger.error(e.getMessage());
        }
    }
    
    private void appendFile(InputStream in, File destFile)
    {
        OutputStream out = null;
        try
        {
            if (destFile.exists())
            {
                out = new BufferedOutputStream(new FileOutputStream(destFile, true), BUFFER_SIZE);
            }
            else
            {
                out = new BufferedOutputStream(new FileOutputStream(destFile), BUFFER_SIZE);
            }
            in = new BufferedInputStream(in, BUFFER_SIZE);
            
            int len;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((len = in.read(buffer)) > 0)
            {
                out.write(buffer, 0, len);
            }
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
        }
        finally
        {
            try
            {
                if (null != in)
                {
                    in.close();
                }
            }
            catch (IOException e)
            {
                logger.error(e.getMessage());
            }
            try
            {
                if (null != out)
                {
                    out.close();
                }
            }
            catch (IOException e)
            {
                logger.error(e.getMessage());
            }
        }
    }
}
