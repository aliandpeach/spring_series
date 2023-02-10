package com.yk.bitcoin.model;

import com.yk.base.valid.GroupConstant;
import com.yk.base.valid.HexValid;
import com.yk.base.valid.TaskFormValid;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@TaskFormValid(groups = GroupConstant.A.class)
@Data
public class TaskForm
{
    @Min(0)
    @Max(1)
    private int type; // 0 hex 1 random

    @HexValid(groups = GroupConstant.B.class)
    private String min;

    @HexValid(groups = GroupConstant.B.class)
    private String max;

    private int state; // 0 停止 1 启动 2 暂停
}
