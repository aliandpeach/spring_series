package com.yk.bitcoin.model;

import com.yk.base.valid.HexValid;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class TaskForm
{
    private int type; // 0 hex 1 random

    @NotNull
    @NotEmpty
    @HexValid
    private String min;

    @NotNull
    @NotEmpty
    @HexValid
    private String max;

    private int state; // 0 停止 1 启动 2 暂停
}
