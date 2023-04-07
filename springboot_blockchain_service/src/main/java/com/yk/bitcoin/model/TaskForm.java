package com.yk.bitcoin.model;

import com.yk.base.valid.GroupConstant;
import com.yk.base.valid.HexValid;
import com.yk.base.valid.TaskFormValid;
import com.yk.base.valid.TaskTypeValid;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@TaskFormValid(groups = GroupConstant.A.class)
@Data
public class TaskForm
{
    @Min(message = "非法的类型", value = 0, groups = GroupConstant.C.class)
    @Max(message = "非法的类型", value = 1, groups = GroupConstant.C.class)
    @TaskTypeValid(message = "任务未启动", groups = GroupConstant.D.class)
    private int type; // 0 hex 1 random

    @HexValid(message = "非法的16进制字符串0", groups = GroupConstant.B.class)
    private String min;

    @HexValid(message = "非法的16进制字符串1", groups = GroupConstant.B.class)
    private String max;

    private int state; // 0 停止 1 启动 2 暂停
}
