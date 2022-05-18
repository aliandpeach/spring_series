import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class JcraftTest
{
    private ChannelSftp channel;
    private Session session;

    @Before
    public void before() throws JSchException, SftpException
    {
        try
        {
            JSch jsch = new JSch();
            jsch.addIdentity("C:\\Users\\yangkai\\.ssh\\test205\\id_rsa", "");
            Session session = jsch.getSession("root", "192.190.116.205", 8022);
//            session.setPassword("123456");
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
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    public void uploadSftp() throws SftpException
    {
        String dst = "/opt/sftp_test_data/download_1.zip";
        this.channel.put("D:\\download_1.zip", dst, null, ChannelSftp.OVERWRITE);

    }
}
