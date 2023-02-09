package com.yk.bitcoin.model;

import lombok.Data;

import java.math.BigInteger;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

@Data
public class Task
{
    public Task(String name)
    {
        this.name = name;
    }

    public Task(String name, BigInteger min, BigInteger max)
    {
        this.name = name;
        this.min = min;
        this.max = max;
    }

    public Task(String name, BigInteger min, BigInteger max, int state)
    {
        this.name = name;
        this.min = min;
        this.max = max;
        this.state = state;
    }

    private String name;

    private BigInteger min;

    private BigInteger max;

    private int state; // 0 停止 1 启动 2 暂停

    private ExecutorService producerService;

    private ExecutorService consumerService;

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name);
    }
}
