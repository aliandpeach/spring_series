package com.yk.demo.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
    @Async("threadPool")
    public void taskRun() {

    }
}
