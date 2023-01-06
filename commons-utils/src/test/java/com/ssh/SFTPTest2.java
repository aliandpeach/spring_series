package com.ssh;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import org.junit.Test;

import java.io.File;
import java.util.Properties;
import java.util.Vector;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2022/11/11 11:50:00
 */
public class SFTPTest2
{
    @Test
    public void testPermission() throws JSchException, SftpException
    {
        String privateKey = null;
        JSch jsch = new JSch();
        if (privateKey != null)
        {
            jsch.addIdentity(privateKey);
        }

        Session session = jsch.getSession("yangkai2", "192.168.20.252", 22);
        session.setPassword("Admin@0123");
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");

        session.setConfig(config);
        session.connect(3600 * 1000);

        Channel channel = session.openChannel("sftp");
        channel.connect();

        ChannelSftp sftp = (ChannelSftp) channel;

        String f = new File("/").getParent();
        SftpATTRS attrs = sftp.lstat("/opt/yangkai2");
        int permissions = attrs.getPermissions();
        boolean writer = (permissions & 00400) != 0; // 文件或文件夹所有者的写权限 (和当前登录用户无关)
        boolean read = (permissions & 00200) != 0; // 文件或文件夹所有者的读权限 (和当前登录用户无关)
        boolean u = attrs.getUId() != 0; // 文件或文件夹所有者是否是root

        // (writer && read && u) 为true只能保证文件或文件夹所有者有读写权限, 不能证明当前ssh的登录者也有权限

        // -rwxr-xr-x 一个普通文件，它的用户类具有完全权限，其组和其他类只有读取和执行权限
        // dr-x------：一个目录，其用户类具有读取和执行权限，其组和其他类没有权限
        // 第一个标识文件或者目录, 其余三组rwx, 分别表示文件所有者的权限, 文件所在组的权限, 其他的权限
        System.out.println(attrs.toString());

        sftp.mkdir("/opt/yangkai2/1234");
        sftp.rmdir("/opt/yangkai2/1234");
    }
}
