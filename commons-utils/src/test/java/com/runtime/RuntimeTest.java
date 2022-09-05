package com.runtime;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2022/08/23 12:07:55
 */
public class RuntimeTest
{
    public static void main(String[] args) throws IOException
    {
        ProcessBuilder pb = new ProcessBuilder("script.sh", "parameter");
        pb.directory(new File("/tmp"));
        // 设置子进程IO输出重定向到指定文件
        // 错误输出与标准输出,输出到一块
        pb.redirectErrorStream(true);
        // 设置输出日志
        pb.redirectOutput(ProcessBuilder.Redirect.appendTo(new File("/tmp/exec_tmp.log")));
        // 执行命令进程
        pb.start();
    }
    public static void main2(String[] args) throws IOException
    {
        CommandLine cmdLine = new CommandLine("nohup" + " " + "command" + " " + System.getProperty("catalina.home") + " " + "&");
        File file = new File("/tmp/restarttm");
        if (!file.exists())
        {
            //创建文件
            file.createNewFile();
        }
        OutputStream stdout = new FileOutputStream(file);
        OutputStream stderr = new FileOutputStream(file);
        PumpStreamHandler pumpStreamHandler = new PumpStreamHandler(stdout, stderr);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(pumpStreamHandler);
        executor.setWorkingDirectory(new File(System.getProperty("catalina.home")));
        executor.execute(cmdLine);
    }
}
