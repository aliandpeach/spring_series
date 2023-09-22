package com.yk.base;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class TaskExecutorConfig implements AsyncConfigurer {

    @Bean("threadPool")
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(1);
    }
}
