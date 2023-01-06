package com.ftp;

import cn.hutool.core.io.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2022/09/28 10:47:26
 */
public class FtpClient
{

    private final String host;    //ftp服务器地址
    private final int port;        //ftp服务器端口
    private final String username;    //用户名
    private final String passwd;    //密码
    private boolean isConnected = false;

    private final FTPClient client;

    public FtpClient(String host, int port, String username, String passwd)
    {
        this.host = host;
        this.port = port;
        this.username = username;
        this.passwd = passwd;
        this.client = new FTPClient();
    }

    public boolean _connect() throws SocketException, IOException
    {
        client.connect(host, port);
        client.login(username, passwd);
        int reply = client.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply))
        {
            client.disconnect();
            isConnected = false;
            return false;
        }
        else
        {
            isConnected = true;
            client.setFileType(FTP.BINARY_FILE_TYPE);
        }
        return true;
    }

    public void upload(String workDir, File file) throws SocketException, IOException
    {
        FileInputStream fis = null;
        try
        {
            if (_connect())
            {
                client.changeWorkingDirectory(workDir);
//				client.setControlEncoding("UTF-8");
                client.enterLocalPassiveMode();
                fis = new FileInputStream(file);
                boolean result = client.storeFile(file.getName(), fis);
            }
        }
        finally
        {
            if (fis != null)
            {
                fis.close();
            }
            _disconnect();
        }
    }

    public void download(String downloadFile, String outputFile, String workDir) throws IOException
    {
        BufferedOutputStream bos = null;
        try
        {
            if (_connect())
            {
                bos = new BufferedOutputStream(new FileOutputStream(outputFile));
                client.changeWorkingDirectory(workDir);
                boolean result = client.retrieveFile(downloadFile, bos);
            }
        }
        finally
        {
            if (bos != null)
            {
                bos.close();
            }
            _disconnect();
        }
    }

    public void uploadDir(String localDir, String remoteDir) throws SocketException, IOException
    {
        try
        {
            if (!isConnected)
            {
                _connect();
            }
            if (null == remoteDir)
            {
                return;
            }
            String workingDir = client.printWorkingDirectory();
            remoteDir = !remoteDir.startsWith(workingDir)
                    ? (workingDir + (remoteDir.startsWith("/") ? remoteDir : "/" + remoteDir))
                    : remoteDir;
            client.changeWorkingDirectory(remoteDir);

            List<File> files = FileUtil.loopFiles(localDir);
            if (null == files)
            {
                return;
            }

            for (File _file : files)
            {
                if (_file.isDirectory())
                {
                    continue;
                }
                try (FileInputStream fis = new FileInputStream(_file))
                {
                    boolean result = client.storeFile(new String(_file.getName().getBytes("GBK"), StandardCharsets.ISO_8859_1), fis);
                }
            }
        }
        finally
        {
            if (isConnected)
            {
                _disconnect();
            }
        }
    }

    public void downloadDir(String localDir, String remoteDir) throws SocketException, IOException
    {
        String rootDir = localDir;

        if (StringUtils.isNotBlank(remoteDir) && !"/".equals(remoteDir))
        {
            if (remoteDir.startsWith("/"))
            {
                rootDir = localDir + File.separator + new String(remoteDir.getBytes("ISO-8859-1"), "GBK").substring(1);
            }
            else
            {
                rootDir = localDir + File.separator + new String(remoteDir.getBytes("ISO-8859-1"), "GBK");
            }
        }

        if (!rootDir.equals(localDir))
        {
            File file = new File(rootDir);
            if (!file.exists())
            {
                file.mkdirs();
            }
            else
            {
                delFolder(localDir);
                file.mkdirs();
            }
        }

        try
        {
            if (!isConnected)
            {
                _connect();
            }

            client.changeWorkingDirectory(remoteDir);
            client.enterLocalPassiveMode();
            FTPFile[] files = client.listFiles();
            if (files == null || files.length == 0)
            {
                return;
            }
            for (int i = 0; i < files.length; i++)
            {
                if (!files[i].isDirectory())
                {
                    BufferedOutputStream bos = null;
                    String fileName = files[i].getName();
                    try
                    {
                        bos = new BufferedOutputStream(new FileOutputStream(
                                rootDir + File.separator + new String(fileName.getBytes("ISO-8859-1"), "GBK")));
                        boolean result = client.retrieveFile(fileName, bos);
                    }
                    finally
                    {
                        if (bos != null)
                        {
                            bos.close();
                        }
                    }
                }
            }

            for (int i = 0; i < files.length; i++)
            {
                if (files[i].isDirectory())
                {
                    String folder = files[i].getName();
                    if (!".".equals(folder) && !"..".equals(folder))
                    {
                        downloadDir(localDir, remoteDir + folder + "/");
                    }
                }
            }
        }
        finally
        {
            if (isConnected)
            {
                _disconnect();
            }
        }
    }

    // 删除文件夹
    // param folderPath 文件夹完整绝对路径
    public static void delFolder(String folderPath)
    {
        try
        {
            delAllFile(folderPath); // 删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete(); // 删除空文件夹
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // 删除指定文件夹下所有文件
    // param path 文件夹完整绝对路径
    public static boolean delAllFile(String path)
    {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists())
        {
            return flag;
        }
        if (!file.isDirectory())
        {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++)
        {
            if (path.endsWith(File.separator))
            {
                temp = new File(path + tempList[i]);
            }
            else
            {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile())
            {
                temp.delete();
            }
            if (temp.isDirectory())
            {
                delAllFile(path + File.separator + tempList[i]);// 先删除文件夹里面的文件
                delFolder(path + File.separator + tempList[i]);// 再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

    public boolean checkPath(String remoteDir)
    {
        boolean flag = false;
        String parentDir = "";
        String subDir = "";
        try
        {
            if (!isConnected)
            {
                _connect();
            }
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
            client.changeWorkingDirectory(parentDir);
            FTPFile[] files = client.listFiles();
            if (files == null || files.length == 0)
            {
                return false;
            }
            else
            {
                for (int i = 0; i < files.length; i++)
                {
                    if (StringUtils.isNotEmpty(subDir))
                    {
                        if (files[i].isDirectory() && subDir.equalsIgnoreCase(files[i].getName()))
                        {
                            client.changeWorkingDirectory(remoteDir);
                            FTPFile[] subfiles = client.listFiles();
                            if (subfiles == null || subfiles.length == 0)
                            {
                            }
                            else
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
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (isConnected)
            {
                try
                {
                    _disconnect();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        return flag;
    }

    public void _disconnect() throws IOException
    {
        if (client != null && client.isConnected())
        {
            client.logout();
            client.disconnect();
            isConnected = false;
        }
    }

    public boolean isConnected()
    {
        return isConnected;
    }

    public void setConnected(boolean isConnected)
    {
        this.isConnected = isConnected;
    }

    public String rm(String remoteDir, String fileName) throws IOException
    {
        try
        {
            if (!isConnected)
            {
                _connect();
            }
            String workingDir = client.printWorkingDirectory();
            remoteDir = null != remoteDir && !remoteDir.startsWith(workingDir)
                    ? (workingDir + (remoteDir.startsWith("/") ? remoteDir : "/" + remoteDir))
                    : remoteDir;
            client.changeWorkingDirectory(remoteDir);
            client.deleteFile(fileName);
            return remoteDir;
        }
        finally
        {
            if (isConnected)
            {
                _disconnect();
            }
        }
    }

    public String mkdir(String remoteDir) throws IOException
    {
        try
        {
            if (!isConnected)
            {
                _connect();
            }
            String workingDir = client.printWorkingDirectory();
            remoteDir = null != remoteDir && !remoteDir.startsWith(workingDir)
                    ? (workingDir + (remoteDir.startsWith("/") ? remoteDir : "/" + remoteDir))
                    : remoteDir;
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
                    client.mkd(partialDirPath.toString());
                }
                catch (IOException e)
                {
                }
            }
            client.changeWorkingDirectory(remoteDir);
            return remoteDir;
        }
        finally
        {
            if (isConnected)
            {
                _disconnect();
            }
        }
    }
}

