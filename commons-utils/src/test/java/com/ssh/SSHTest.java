package com.ssh;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2022/09/20 11:12:09
 */
public class SSHTest
{
    /**
     * 退格
     */
    private static final String BACKSPACE = new String(new byte[]{8});

    /**
     * ESC
     */
    private static final String ESC = new String(new byte[]{27});

    /**
     * 空格
     */
    private static final String BLANKSPACE = new String(new byte[]{32});

    /**
     * 回车
     */
    private static final String ENTER = new String(new byte[]{13});

    /**
     * 某些设备回显数据中的控制字符
     */
    private static final String[] PREFIX_STRS = {BACKSPACE + "+" + BLANKSPACE + "+" + BACKSPACE + "+",
            "(" + ESC + "\\[\\d+[A-Z]" + BLANKSPACE + "*)+"};

    private final int sleepTime = 200;

    /**
     * 保存当前命令的回显信息
     */
    protected StringBuffer currEcho;

    /**
     * 保存所有的回显信息
     */
    protected StringBuffer totalEcho;

    private final String ip;
    private final int port;
    private String endEcho = "#,?,>,:";
    private String moreEcho = "---- More ----";
    private final String moreCmd = BLANKSPACE;
    private Session session;
    private Channel channel;

    public void run()
    {
        InputStream is;
        try
        {
            is = channel.getInputStream();
            String echo = readOneEcho(is);
            while (echo != null)
            {
                currEcho.append(echo);
                String[] lineStr = echo.split("\\n");
                if (lineStr.length > 0)
                {
                    String lastLineStr = lineStr[lineStr.length - 1];
                    if (lastLineStr != null && lastLineStr.indexOf(moreEcho) > 0)
                    {
                        totalEcho.append(echo.replace(lastLineStr, ""));
                    }
                    else
                    {
                        totalEcho.append(echo);
                    }
                }
                echo = readOneEcho(is);
            }
            System.out.println();
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }

    protected String readOneEcho(InputStream instr)
    {
        byte[] buff = new byte[1024];
        int ret_read = 0;
        try
        {
            ret_read = instr.read(buff);
        }
        catch (IOException e)
        {
            return null;
        }
        if (ret_read > 0)
        {
            String result = new String(buff, 0, ret_read);
            for (String PREFIX_STR : PREFIX_STRS)
            {
                result = result.replaceFirst(PREFIX_STR, "");
            }
            try
            {
                return new String(result.getBytes(), "GBK");
            }
            catch (UnsupportedEncodingException e)
            {
                System.out.println(e.getMessage());
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    public SSHTest(String ip, int port, String endEcho, String moreEcho)
    {
        this.ip = ip;
        this.port = port;
        if (endEcho != null)
        {
            this.endEcho = endEcho;
        }
        if (moreEcho != null)
        {
            this.moreEcho = moreEcho;
        }
        totalEcho = new StringBuffer();
        currEcho = new StringBuffer();
    }

    private void close()
    {
        if (session != null)
        {
            session.disconnect();
        }
        if (channel != null)
        {
            channel.disconnect();
        }
    }

    private boolean login(String[] cmds)
    {
        String user = cmds[0];
        String passWord = cmds[1];
        JSch jsch = new JSch();
        try
        {
            session = jsch.getSession(user, this.ip, this.port);
            session.setPassword(passWord);
            UserInfo ui = new SSHUserInfo()
            {
                public void showMessage(String message)
                {
                }

                public boolean promptYesNo(String message)
                {
                    return true;
                }
            };
            session.setUserInfo(ui);
            session.connect(30000);
            channel = session.openChannel("shell");
            channel.connect(3000);
            new Thread(this::run).start();
            return true;
        }
        catch (JSchException e)
        {
            System.out.println(e.getMessage());
            return false;
        }
    }

    protected void sendCommand(String command, boolean sendEnter)
    {
        try
        {
            OutputStream os = channel.getOutputStream();
            os.write(command.getBytes());
            os.flush();
            if (sendEnter)
            {
                currEcho = new StringBuffer();
                os.write(ENTER.getBytes());
                os.flush();
            }
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }

    protected boolean containsEchoEnd(String echo)
    {
        boolean contains = false;
        if (endEcho == null || endEcho.trim().equals(""))
        {
            return false;
        }
        String[] eds = endEcho.split(",");
        for (String ed : eds)
        {
            if (echo.trim().endsWith(ed))
            {
                contains = true;
                break;
            }
        }
        return contains;
    }

    private String runCommand(String command, boolean ifEnter)
    {
        currEcho = new StringBuffer();
        sendCommand(command, ifEnter);
        int time = 0;
        /**
         * 连接超时(单次命令总耗时)
         */
        int timeout = 4000;
        if (endEcho == null || endEcho.equals(""))
        {
            while (currEcho.toString().equals(""))
            {
                try
                {
                    Thread.sleep(sleepTime);
                    time += sleepTime;
                    if (time >= timeout)
                    {
                        break;
                    }
                }
                catch (InterruptedException e)
                {
                    System.out.println(e.getMessage());
                }
            }
        }
        else
        {
            while (!containsEchoEnd(currEcho.toString()))
            {
                try
                {
                    Thread.sleep(sleepTime);
                }
                catch (InterruptedException e)
                {
                    System.out.println(e.getMessage());
                }
                time += sleepTime;
                if (time >= timeout)
                {
                    break;
                }
                String[] lineStrs = currEcho.toString().split("\\n");
                if (lineStrs.length > 0)
                {
                    if (moreEcho != null && lineStrs[lineStrs.length - 1] != null
                            && lineStrs[lineStrs.length - 1].contains(moreEcho))
                    {
                        sendCommand(moreCmd, false);
                        currEcho.append("\n");
                        time = 0;
                    }
                }
            }
        }
        return currEcho.toString();
    }

    private String batchCommand(String[] cmds, int[] othernEenterCmds)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 2; i < cmds.length; i++)
        {
            String cmd = cmds[i];
            if (cmd.equals(""))
            {
                continue;
            }
            boolean ifInputEnter = false;
            if (othernEenterCmds != null)
            {
                for (int c : othernEenterCmds)
                {
                    if (c == i)
                    {
                        ifInputEnter = true;
                        break;
                    }
                }
            }
            cmd += (char) 10;
            String resultEcho = runCommand(cmd, ifInputEnter);
            sb.append(resultEcho);
        }
        close();
        return totalEcho.toString();
    }

    public String executive(String[] cmds, int[] othernEenterCmds)
    {
        if (cmds == null || cmds.length < 3)
        {
            return null;
        }
        if (login(cmds))
        {
            return batchCommand(cmds, othernEenterCmds);
        }
        return null;
    }

    private abstract static class SSHUserInfo implements UserInfo, UIKeyboardInteractive
    {
        public String getPassword()
        {
            return null;
        }

        public boolean promptYesNo(String str)
        {
            return true;
        }

        public String getPassphrase()
        {
            return null;
        }

        public boolean promptPassphrase(String message)
        {
            return true;
        }

        public boolean promptPassword(String message)
        {
            return true;
        }

        public void showMessage(String message)
        {
        }

        public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt,
                                                  boolean[] echo)
        {
            return null;
        }
    }

    /**
     * 获取到命令最终所需值
     *
     * @param ip
     * @param username
     * @param password
     * @param comd
     * @return
     */
    public static String getFinaleComdResult(String ip, String username, String password, String comd)
    {
        int port = 22;
        SSHTest sshTest = new SSHTest(ip, port, null, null);
        String[] cmds = {username, password, comd};
        String executive = sshTest.executive(cmds, null);
        String[] re = executive.split("\n");
        String result = re[re.length - 2];
        return result.replaceAll("\r", "");
    }

    public static void impdp(String dbUser, String dbPasswd, String dumpPath, String dumpName, String dumpUser, String ip, String username, String password, String instanceName)
    {
        String oracleTab = "export ORACLE_SID=" + instanceName;
        String dumpPathName = "dump_dir";
        String logFile = "impdp_" + System.currentTimeMillis() + ".log";
        int port = 22;
        SSHTest sshTest = new SSHTest(ip, port, null, null);
        String[] cmds = {username, password,
                "source /home/oracle/.bash_profile",
                oracleTab, "sqlplus -S /nolog ",
                "conn / as sysdba", "SET SERVEROUTPUT ON",
                "SET HEADING OFF", "SET FEEDBACK OFF",
                "CREATE OR REPLACE directory " + dumpPathName + " AS '" + dumpPath + "';",
                "GRANT READ, WRITE ON directory " + dumpPathName + " TO " + dbUser + ";",
                "quit;",
                "impdp " + dbUser + "/" + dbPasswd + "@" + ip + "/" + instanceName + " directory=" + dumpPathName + "  dumpfile=" + dumpName + "  remap_schema = " + dumpUser + ":" + dbUser + " statistics=N table_exists_action=replace   logfile=" + logFile};
        String executive = sshTest.executive(cmds, null);
    }

    public static void main(String[] args)
    {
//        SSHTest.impdp("TEST_ONE", "Admin#0123", );
        SSHTest.expdp("TEST_DUMP",
                "TEST_DUMP", "Admin#0123",
                "/opt/yangkai", "192.168.31.19", 22, "oracle", "Admin@0123", "orcl", null);
    }

    public static void expdp(String dbUser, String owner, String dbPasswd, String dumpPath, String ip, int sshPort, String username, String password, String instanceName, String tables)
    {
        String oracleTab = "export ORACLE_SID=" + instanceName;
        String dmpName = dbUser + ".dmp";
        String oldFile = dumpPath + "/" + dmpName;
        String dumpPathName = "dump_dir";
        String logName = "expdp_" + System.currentTimeMillis() + ".log";

        if (StringUtils.isEmpty(owner))
        {
            owner = dbUser;
        }
        String target = "schemas=" + owner;
        if (StringUtils.isNotEmpty(tables))
        {
            target = "tables=" + tables;
        }

        SSHTest sshTest = new SSHTest(ip, sshPort, null, null);
        String[] cmds = {username, password,
                "source /home/oracle/.bash_profile",
                "if [ -f " + oldFile + " ]", "then",
                "rm -rf " + oldFile + "", "fi",
                oracleTab, "echo $ORACLE_SID", "sqlplus -S /nolog <<EOF\n" +
                "conn / as sysdba\n" + "SET SERVEROUTPUT ON;\n" +
                "SET HEADING OFF;\n" + "SET FEEDBACK OFF;\n" +
                "CREATE OR REPLACE directory " + dumpPathName + " AS '" + dumpPath + "';\n" +
                "GRANT READ, WRITE ON directory " + dumpPathName + " TO " + dbUser + ";\n" +
                "quit;\n" + "EOF",
                "expdp  " + dbUser + "/" + dbPasswd + "@" + ip + "/" + instanceName + " " + target + " DIRECTORY=" + dumpPathName + " DUMPFILE=" + dmpName + "  buffer=50000000  logfile=" + logName};
        String executive = sshTest.executive(cmds, null);
    }


    public static void comod()
    {
        String ip = "192.168.0.1";
        int port = 22;
        SSHTest sshTest = new SSHTest(ip, port, null, null);
        String username = "oracle";
        String password = "oracle";
//        String cmd="df -h /adfsoft/dump/";
        String cmd = "df  /yangkai/dump| awk '{print $4}'";  //获取服务器空间
        String dmpSize = "du /yangkai/dump/sss1.dmp |awk '{print $1 }'";//dmp文件大小
        String isoracle = "ll /home/oracle | grep yr |awk '{print $3 }'";//oracle 群组判断
        String[] cmds = {username, password, isoracle};
        String executive = sshTest.executive(cmds, null);
        System.out.println("返回：" + executive);
        System.out.println("-----------------------------------");
        String[] re = executive.split("\n");
        String kj = re[re.length - 2];
    }
}
