package com.ssh;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.Vector;

public class SFTPUtil
{
    private ChannelSftp sftp;

    private Session session;
    /**
     * FTP 登录用户名
     */
    private String username;
    /**
     * FTP 登录密码
     */
    private String password;
    /**
     * 私钥
     */
    private String privateKey;
    /**
     * FTP 服务器地址IP地址
     */
    private String host;
    /**
     * FTP 端口
     */
    private int port;


    public SFTPUtil(String username, String password, String host, int port)
    {
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
    }

    public SFTPUtil(String username, String host, int port, String privateKey)
    {
        this.username = username;
        this.host = host;
        this.port = port;
        this.privateKey = privateKey;
    }

    public SFTPUtil()
    {
    }

    /**
     * 连接sftp服务器
     *
     * @throws Exception
     */
    public void login()
    {
        try
        {
            JSch jsch = new JSch();
            if (privateKey != null)
            {
                jsch.addIdentity(privateKey);// 设置私钥
            }

            session = jsch.getSession(username, host, port);
            if (password != null)
            {
                session.setPassword(password);
            }
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");

            session.setConfig(config);
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();

            sftp = (ChannelSftp) channel;
        }
        catch (JSchException e)
        {
        }
    }

    /**
     * 关闭连接 server
     */
    public void logout()
    {
        if (sftp != null)
        {
            if (sftp.isConnected())
            {
                sftp.disconnect();
            }
        }
        if (session != null)
        {
            if (session.isConnected())
            {
                session.disconnect();
            }
        }
    }

    public void upload(String directory, String sftpFileName, InputStream input) throws SftpException
    {
        try
        {
            sftp.cd(directory);
        }
        catch (SftpException e)
        {
            sftp.mkdir(directory);
            sftp.cd(directory);
        }
        sftp.put(input, sftpFileName);
    }

    public void upload(String directory, String uploadFile) throws FileNotFoundException, SftpException
    {
        File file = new File(uploadFile);
        upload(directory, file.getName(), new FileInputStream(file));
    }

    public void upload(String directory, String sftpFileName, byte[] byteArr) throws SftpException
    {
        upload(directory, sftpFileName, new ByteArrayInputStream(byteArr));
    }

    public void upload(String directory, String sftpFileName, String dataStr, String charsetName) throws UnsupportedEncodingException, SftpException
    {
        upload(directory, sftpFileName, new ByteArrayInputStream(dataStr.getBytes(charsetName)));
    }

    public void download(String directory, String downloadFile, String saveFile) throws SftpException, FileNotFoundException
    {
        if (directory != null && !"".equals(directory))
        {
            sftp.cd(directory);
        }
        //判断文佳是否存在
        String path = saveFile.replace(downloadFile, "");
        File fileExists = new File(path);
        if (!fileExists.exists())
        {
            fileExists.mkdirs();
        }
        File file = new File(saveFile);
        sftp.get(downloadFile, new FileOutputStream(file));
    }

    public byte[] download(String directory, String downloadFile) throws SftpException, IOException
    {
        if (directory != null && !"".equals(directory))
        {
            sftp.cd(directory);
        }
        InputStream is = sftp.get(downloadFile);

        byte[] fileData = IOUtils.toByteArray(is);

        return fileData;
    }

    public void delete(String directory, String deleteFile) throws SftpException
    {
        sftp.cd(directory);
        sftp.rm(deleteFile);
    }

    public Vector<LsEntry> listFiles(String directory) throws SftpException
    {
        return sftp.ls(directory);
    }

    public void recursiveFolderDownload(String machinename, String path) throws SftpException, FileNotFoundException
    {
        Vector<LsEntry> fileList = sftp.ls(machinename);
        for (LsEntry file : fileList)
        {
            if (!file.getFilename().equals(".") && !file.getFilename().equals(".."))
            {
                String localpath = path + File.separator + file.getFilename();
                if (!file.getAttrs().isDir())
                { // Check if it is a file (not a directory).
                    download(machinename, file.getFilename(), localpath);
                }
                else
                {
                    String pathname = machinename + "/" + file.getFilename();
                    recursiveFolderDownload(pathname, path + "/" + file.getFilename());
                }
            }
        }
    }

    public boolean checkPath(String remoteDir)
    {
        boolean flag = false;
        String parentDir = "";
        String subDir = "";
        try
        {
            // 切换工作路径
            if ("/".equalsIgnoreCase(remoteDir))
            {
                parentDir = "/";
            }
            else
            {
                subDir = remoteDir.substring(remoteDir.lastIndexOf("/") + 1);
                parentDir = remoteDir.substring(0, remoteDir.length() - subDir.length());
            }
            sftp.cd(parentDir);
            Vector<LsEntry> files = sftp.ls(parentDir);
            if (files != null && files.size() > 0)
            {
                for (LsEntry file : files)
                {
                    if (StringUtils.isNotEmpty(subDir))
                    {
                        if (subDir.equalsIgnoreCase(file.getFilename()))
                        {
                            sftp.cd(remoteDir);
                            Vector<LsEntry> subfiles = sftp.ls(remoteDir);
                            if (subfiles != null && subfiles.size() > 0)
                            {
                                flag = true;
                                break;
                            }
                        }
                    }
                    else
                    {
                        flag = true;
                        break;
                    }
                }
            }
        }
        catch (SftpException e)
        {
            e.printStackTrace();
        }

        return flag;
    }

    public void mkdir(String remoteDir) throws SftpException
    {
        String[] folders = remoteDir.split("/");
        StringBuilder partialDirPath = new StringBuilder("");
        for (String folder : folders)
        {
            if (folder.length() == 0)
            {
                continue;
            }
            partialDirPath.append("/").append(folder);
            try
            {
                sftp.mkdir(partialDirPath.toString());
            }
            catch (SftpException e)
            {
            }
        }
        sftp.cd(remoteDir);
    }
}