package com.yk.connector.sftp;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.yk.core.IExecutorService;
import com.yk.core.Request;
import com.yk.core.Response;
import com.yk.exception.SdkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

/**
 * FTP请求服务类
 *
 * @author yangk
 * @version 1.0
 * @since 2021/5/24 14:25
 */
public class FtpExecutorServiceImpl implements IExecutorService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FtpExecutorServiceImpl.class);

    private static final Map<FtpRequest, Semaphore> SFTP_CONNECTION_CACHE = new ConcurrentHashMap<>();

    private static final FtpExecutorListener listener = new FtpExecutorListener()
    {
    };

    @Override
    public Response execute(Request request)
    {
        listener.preCheck(null == request || null == request.getFileInfo() ? null : request.getFileInfo().getJobId());
        try
        {
            Semaphore semaphore = SFTP_CONNECTION_CACHE.computeIfAbsent((FtpRequest) request, t -> new Semaphore(3));
            String directory;
            try
            {
                semaphore.acquire();
                SftpCommand sftp = new SftpCommand((FtpRequest) request);
                directory = sftp.send();
                LOGGER.debug("文件{}上传完成", request.getFileInfo().getPath());
            }
            finally
            {
                semaphore.release();
            }
            if (null != request.getListener()
                    && request.getListener() instanceof FtpExecutorListener)
            {
                LOGGER.info("SFTP服务器文件上传成功 {}", directory);
                LOGGER.info("下发任务, 服务端开始解压缩 {}", request.getFileInfo().getPath());
                FtpRequest ftpRequest = (FtpRequest) request;
                ftpRequest.setDirectory(directory);
                return ((FtpExecutorListener) request.getListener()).thenAnalyze(ftpRequest);
            }
            LOGGER.info("---SFTP 文件上传成功 {}", directory);
            return new Response();
        }
        catch (JSchException | SftpException e)
        {
            LOGGER.error("send file failed", e);
            throw new SdkException("上传大文件失败", e);
        }
        catch (SdkException e)
        {
            LOGGER.error("send file error", e);
            throw e;
        }
        catch (Throwable e)
        {
            LOGGER.error("send file error", e);
            throw new SdkException("上传大文件出错", e);
        }
    }

    @Override
    public String getType()
    {
        return "ftp";
    }
}
