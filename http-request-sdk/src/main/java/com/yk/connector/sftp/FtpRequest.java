package com.yk.connector.sftp;

import com.yk.connector.http.HttpRequest;
import com.yk.core.FileInfo;
import com.yk.core.Request;
import com.yk.core.SdkExecutors;
import com.yk.mq.MessageTaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * FTP参数组装
 *
 * @author yangk
 * @version 1.0
 * @since 2021/5/24 14:25
 */
public class FtpRequest extends Request
{
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequest.class);

    private String username;

    private String passwd;

    private String ip;

    private int port;

    private String directory;

    private String identity;

    FtpRequest()
    {
        super("ftp");
    }

    public String getUsername()
    {
        return username;
    }

    public String getPasswd()
    {
        return passwd;
    }

    public String getIp()
    {
        return ip;
    }

    public int getPort()
    {
        return port;
    }

    public String getIdentity()
    {
        return identity;
    }

    public String getDirectory()
    {
        return directory;
    }

    public void setDirectory(String directory)
    {
        this.directory = directory;
    }

    public static FtpRequest.RequestBuilder upload()
    {
        return new FtpRequest.RequestBuilder();
    }

    public static final class RequestBuilder
    {

        FtpRequest request;

        public RequestBuilder()
        {
            this.request = new FtpRequest();
        }

        /**
         * 自定义SFTP的登录账户， 对应配置文件中的ftp.username 配置
         * 这里配置后，配置文件ftp.username 将不生效
         */
        public FtpRequest.RequestBuilder username(String username)
        {
            this.request.username = username;
            return this;
        }

        /**
         * 自定义SFTP的登录密码， 对应配置文件中的ftp.password 配置
         * 这里配置后，配置文件ftp.password 将不生效
         */
        public FtpRequest.RequestBuilder passwd(String passwd)
        {
            this.request.passwd = passwd;
            return this;
        }

        /**
         * 自定义SFTP的端口， 对应配置文件中的ftp.port 配置
         * 这里配置后，配置文件ftp.port 将不生效
         */
        public FtpRequest.RequestBuilder port(int port)
        {
            this.request.port = port;
            return this;
        }

        /**
         * 自定义SFTP的IP地址，对应配置文件中的ftp.ip 配置
         * 这里配置后，配置文件ftp.ip 将不生效
         */
        public FtpRequest.RequestBuilder ip(String ip)
        {
            this.request.ip = ip;
            return this;
        }

        /**
         * 自定义SFTP的免密登录私钥路径， 对应配置文件中的ftp.key 配置
         * 这里配置后，配置文件ftp.key 将不生效
         */
        public FtpRequest.RequestBuilder identity(String identity)
        {
            this.request.identity = identity;
            return this;
        }

        /**
         * 配置FTP上传的文件信息
         */
        public FtpRequest.RequestBuilder file(FileInfo fileInfo)
        {
            this.request.fileInfo = fileInfo;
            return this;
        }

        /**
         * 执行下一步文件解析动作
         */
        public FtpRequest.RequestBuilder thenAnalyze()
        {
            this.request.ofListener(new FtpExecutorListener()
            {
            });
            return this;
        }

        /**
         * 是否异步完成请求, 该参数设定为true, 上传SFTP后, 调用的解析接口会直接返回
         *
         * @return RequestBuilder
         */
        public FtpRequest.RequestBuilder async()
        {
            this.request.async = true;
            return this;
        }

        public FtpRequest.RequestBuilder manager(MessageTaskManager manager)
        {
            this.request.taskManager = manager;
            return this;
        }

        /**
         * Builds the FtpRequest
         */
        public FtpRequest build()
        {
            if (null == this.request.username)
            {
                this.request.username = SdkExecutors.create().getCommonInfo().getFtpUsername();
            }
            if (null == this.request.passwd)
            {
                this.request.passwd = SdkExecutors.create().getCommonInfo().getFtpPassword();
            }
            if (null == this.request.ip)
            {
                this.request.ip = SdkExecutors.create().getCommonInfo().getIp();
            }
            if (this.request.port <= 0)
            {
                this.request.port = SdkExecutors.create().getCommonInfo().getPort();
            }
            if (null == this.request.identity)
            {
                this.request.identity = SdkExecutors.create().getCommonInfo().getFtpKey();
            }

            LOGGER.debug("build-ftp-request = {}", request.getFileInfo());
            return request;
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FtpRequest that = (FtpRequest) o;
        return port == that.port && Objects.equals(username, that.username) && Objects.equals(ip, that.ip);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(username, ip, port);
    }
}
