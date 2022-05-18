package com.yk.core;

import cn.hutool.core.io.FileTypeUtil;
import com.yk.auth.LoginAuth;
import com.yk.connector.http.HttpRequest;
import com.yk.connector.sftp.FtpRequest;
import com.yk.event.InitializingContext;
import com.yk.event.InitializingEvent;
import com.yk.exception.SdkException;
import com.yk.mq.MessageCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * 请求执行器
 *
 * @author yangk
 * @version 1.0
 * @since 2021/5/23 10:13
 */
public class SdkExecutors
{
    public static final String[] FTP_FILE_TYPE_ALLOW = new String[]{"zip", "rar", "7z", "xz", "bz2", "gz", "wim", "tar"};

    private static final Logger LOG = LoggerFactory.getLogger(SdkExecutors.class);

    private static SdkExecutors INSTANCE;

    private static final Map<String, IExecutorService> services = new HashMap<>();

    private CommonInfo commonInfo;

    private SdkExecutors()
    {
    }

    public static SdkExecutors create()
    {
        if (null == INSTANCE)
        {
            synchronized (SdkExecutors.class)
            {
                if (null == INSTANCE)
                {
                    INSTANCE = new SdkExecutors();
                }
            }
        }
        return INSTANCE;
    }

    public CommonInfo getCommonInfo()
    {
        return commonInfo;
    }

    public SdkExecutors init(CommonInfo CommonInfo)
    {
        if (null == CommonInfo)
        {
            throw new SdkException("初始化数据信息不能为空");
        }
        commonInfo = CommonInfo;
        // 初始化 ActiveMQ
        InitializingContext.getInstance().next(new InitializingEvent(commonInfo).of(MessageCenter.class.getName()));
        InitializingContext.getInstance().next(new InitializingEvent(commonInfo).of(LoginAuth.class.getName()));
        FileTypeUtil.putFileType("526172211A070100", "rar");
        FileTypeUtil.putFileType("377ABCAF271C000", "7z");
        FileTypeUtil.putFileType("FD377A585A00000", "xz");
        FileTypeUtil.putFileType("1F8B080", "gz");
        FileTypeUtil.putFileType("425A6839314159", "bz2");
        FileTypeUtil.putFileType("4D5357494D", "wim");
        return INSTANCE;
    }

    private synchronized IExecutorService service(String type)
    {
        if (services.containsKey(type)) return services.get(type);

        Iterator<IExecutorService> it = ServiceLoader.load(IExecutorService.class, getClass().getClassLoader()).iterator();
        if (!it.hasNext())
        {
            LOG.error("No connector found in classpath");
            throw new SdkException("No connector found in classpath");
        }
        while (it.hasNext())
        {
            IExecutorService service = it.next();
            services.put(service.getType(), service);
        }
        if (!services.containsKey(type))
        {
            LOG.error("No connector service for type [{}] found in classpath", type);
            throw new SdkException("No connector service for type [ " + type + " ] found in classpath");
        }
        return services.get(type);
    }

    public Response execute(Request request)
    {
        LOG.debug("Executing Request: {} -> {}", request.getType(), request.getFileInfo());
        return service(request.getType()).execute(request);
    }

    /**
     * 0 - 10M
     */
    public Response upload(HttpRequest request)
    {
        LOG.debug("Executing Request: {} -> {}", request.getType(), request.getFileInfo());
        return service(request.getType()).execute(request);
    }

    /**
     * 上传大于10M的文件
     */
    public Response uploadBigFile(FtpRequest request)
    {
        LOG.debug("Executing Request: {} -> {}", request.getType(), request.getFileInfo());
        return service(request.getType()).execute(request);
    }
}
