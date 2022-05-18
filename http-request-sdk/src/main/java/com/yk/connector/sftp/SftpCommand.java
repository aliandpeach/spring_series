package com.yk.connector.sftp;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.HexUtil;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.yk.exception.SdkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import static com.yk.core.SdkExecutors.FTP_FILE_TYPE_ALLOW;

/**
 * FTP命令执行
 *
 * @author yangk
 * @version 1.0
 * @since 2021/5/24 14:25
 */
public class SftpCommand
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SftpCommand.class);

    private static final long MAX = 4 * 1024 * 1024 * 1024L;
    private static final long MIN = 10 * 1024 * 1024L;

    private ChannelSftp channel;
    private Session session;

    private final FtpRequest request;

    public SftpCommand(FtpRequest request)
    {
        this.request = request;
        try
        {
            init();
        }
        catch (JSchException | SftpException | IOException ex)
        {
            LOGGER.error("init sftp error", ex);
            try
            {
                // 重试一次
                LOGGER.error("init sftp repeat ");
                init();
            }
            catch (JSchException | SftpException | IOException e)
            {
                throw new SdkException("初始化SFTP失败", e);
            }
        }
    }

    private void init() throws JSchException, SftpException, IOException
    {
        if (null == request)
        {
            throw new SdkException("SFTP初始化参数为空");
        }
        if (isEmpty(request.getIdentity()) && isEmpty(request.getPasswd()))
        {
            throw new SdkException("SFTP初始化密码和私钥都为空(至少一项输入正确)");
        }
        if (isEmpty(request.getUsername()))
        {
            throw new SdkException("SFTP初始化用户名为空");
        }
        if (isEmpty(request.getIp()))
        {
            throw new SdkException("SFTP初始化IP为空");
        }
        if (request.getPort() < 1 || request.getPort() > 65535)
        {
            throw new SdkException("SFTP初始化端口不正确");
        }
        JSch jsch = new JSch();
        if (null != request.getIdentity() && request.getIdentity().trim().length() != 0)
        {
            jsch.addIdentity(request.getIdentity());
        }
        Session session = jsch.getSession(request.getUsername(), request.getIp(), request.getPort());
        LOGGER.debug("Session created.");
        if (null != request.getPasswd())
        {
            session.setPassword(request.getPasswd());
        }
        Properties props = new Properties();
        props.put("StrictHostKeyChecking", "no");
        session.setConfig(props);
        session.connect();
        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();
        channel.setFilenameEncoding(StandardCharsets.UTF_8.name());

        this.channel = channel;
        this.session = session;
    }

    public String send() throws JSchException, SftpException
    {
        if (null == request.getFileInfo())
        {
            throw new SdkException("文件不能为空");
        }
        if (null == request.getFileInfo().getName() || null == request.getFileInfo().getPath())
        {
            throw new SdkException("文件名或者文件路径不能为空 : " + request.getFileInfo().getPath());
        }
        File upload = new File(request.getFileInfo().getPath());
        if (!upload.exists() || !upload.isFile())
        {
            throw new SdkException("文件不存在或者不是文件 : " + request.getFileInfo().getPath());
        }

        String name = upload.getName();
        int lastPointIndex = name.lastIndexOf(".");
        if (lastPointIndex <= 0)
        {
            throw new SdkException("文件名没有后缀 : " + request.getFileInfo().getPath());
        }

        String suffix = name.substring(lastPointIndex + 1);
        Arrays.stream(FTP_FILE_TYPE_ALLOW).filter(t -> t.equalsIgnoreCase(suffix)).findFirst()
                .orElseThrow(() -> new SdkException("文件压缩格式不支持 : " + request.getFileInfo().getPath()));

        String type = FileUtil.getType(upload);
        boolean tar = tarType(upload);
        if (!tar)
        {
            Arrays.stream(FTP_FILE_TYPE_ALLOW).filter(t -> t.equalsIgnoreCase(type)).findFirst().orElseThrow(() -> new SdkException("不支持该文件格式: " + upload.getName()));
        }

        String limitObj = System.getProperty("limit");
        if (null == limitObj || !limitObj.equalsIgnoreCase("false"))
        {
            if (upload.length() > MAX || upload.length() < MIN)
            {
                throw new SdkException("不能上传大于4G或者小于10M的文件 : " + request.getFileInfo().getPath());
            }
        }

        request.getFileInfo().setSize(upload.length());
        request.getFileInfo().setModifyDate(upload.lastModified());

        try
        {
            BasicFileAttributes attributes = Files.readAttributes(upload.toPath(), BasicFileAttributes.class);
            long milliseconds = attributes.creationTime().to(TimeUnit.MILLISECONDS);
            request.getFileInfo().setCreateDate(milliseconds);
        }
        catch (IOException exception)
        {
            throw new SdkException("文件创建时间获取失败 : " + request.getFileInfo().getPath(), exception);
        }

        try
        {
            String src = request.getFileInfo().getPath();
            String directory = "/sftp_test_data/" + UUID.randomUUID();
            String dst = directory + "/" + request.getFileInfo().getName();
            Vector<ChannelSftp.LsEntry> vector = this.channel.ls("/");
            ChannelSftp.LsEntry data = vector.stream().filter(f -> f.getFilename().equalsIgnoreCase("sftp_test_data")).findFirst().orElse(null);
            if (null == data)
            {
                this.channel.mkdir("/sftp_test_data");
            }
            this.channel.mkdir(directory);
            synchronized (SftpCommand.class)
            {
                this.channel.put(src, dst, null, ChannelSftp.OVERWRITE);
            }
            return directory;
        }
        finally
        {
            if (channel != null && channel.isConnected())
            {
                channel.disconnect();
            }
            if (session != null && session.isConnected())
            {
                session.disconnect();
            }
        }
    }

    public static byte[] readBytes(InputStream in, int offset, int length) throws IOException
    {
        if (null == in)
        {
            return null;
        }
        if (length <= 0)
        {
            return new byte[0];
        }
        if (offset <= 0)
        {
            return new byte[0];
        }

        byte[] b = new byte[length];
        int readLength;
        try
        {
            long len = in.skip(offset);
            readLength = in.read(b);
        }
        catch (IOException e)
        {
            throw e;
        }
        if (readLength > 0 && readLength < length)
        {
            byte[] b2 = new byte[length];
            System.arraycopy(b, 0, b2, 0, readLength);
            return b2;
        }
        else
        {
            return b;
        }
    }

    public static boolean tarType(File upload)
    {
        try (InputStream input = new FileInputStream(upload))
        {
            byte[] reads = readBytes(input, 100, 3);
            String header = HexUtil.encodeHexStr(reads);
            LOGGER.debug("file 100 type : " + header);
            if ("100777".equals(header))
            {
                return true;
            }
        }
        catch (Exception e)
        {
            LOGGER.error("upload file : " + upload.getPath() + " read 100 bytes error", e);
        }
        try (InputStream input = new FileInputStream(upload))
        {
            byte[] reads = readBytes(input, 257, 5);
            String ustar = new String(reads, 0, reads.length);
            LOGGER.debug("file 257 type : " + ustar);
            if ("ustar".equals(ustar))
            {
                return true;
            }
        }
        catch (Exception e)
        {
            LOGGER.error("upload file : " + upload.getPath() + " read 257 bytes error", e);
        }
        return false;
    }

    private boolean isEmpty(String str)
    {
        return null == str || str.trim().length() == 0;
    }
}
