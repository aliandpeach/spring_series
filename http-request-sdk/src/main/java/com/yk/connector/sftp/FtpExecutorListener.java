package com.yk.connector.sftp;

import com.yk.connector.http.HttpRequest;
import com.yk.core.ExecutorListener;
import com.yk.core.Response;
import com.yk.core.SdkExecutors;
import com.yk.exception.SdkException;

import java.util.HashMap;
import java.util.Map;

import static com.yk.connector.http.HttpRequest.PRE_CHECK_JOB_STATUS_URI;

/**
 * FtpExecutorListener
 *
 * @author yangk
 * @version 1.0
 * @since 2021/06/07 11:06:35
 */
public interface FtpExecutorListener extends ExecutorListener
{
    default Response thenAnalyze(FtpRequest ftpRequest)
    {
        if (null == ftpRequest.getDirectory())
        {
            throw new SdkException("SFTP directory id null");
        }
        Map<String, Object> fileInfo = new HashMap<>();
        fileInfo.put("directory", ftpRequest.getDirectory());
        fileInfo.put("name", ftpRequest.getFileInfo().getName());
        fileInfo.put("size", ftpRequest.getFileInfo().getSize() + "");
        fileInfo.put("jobId", ftpRequest.getFileInfo().getJobId());
        fileInfo.put("modifyDate", ftpRequest.getFileInfo().getModifyDate() + "");
        fileInfo.put("createDate", ftpRequest.getFileInfo().getCreateDate() + "");

        HttpRequest.RequestBuilder builder = HttpRequest.analyze().params(fileInfo);

        if (ftpRequest.getTaskManager() != null)
        {
            builder.manager(ftpRequest.getTaskManager());
        }

        HttpRequest httpRequest = ftpRequest.isAsync() ? builder.async().build() : builder.build();
        return SdkExecutors.create().execute(httpRequest);
    }

    default void preCheck(String jobId)
    {
        if (null == jobId)
        {
            throw new SdkException("策略ID为空");
        }
        Map<String, Object> param = new HashMap<>();
        param.put("jobId", jobId);
        HttpRequest httpRequest = HttpRequest.create()
                .uri(PRE_CHECK_JOB_STATUS_URI).method("GET").async()
                .params(param).build();
        Response response = SdkExecutors.create().execute(httpRequest);
        if (response.getStatus() != 200)
        {
            throw new SdkException("外发辅助策略[" + jobId + "]处于暂停状态");
        }
    }
}
