package com.yk.bitcoin.model;

import com.yk.base.valid.GroupConstant;
import com.yk.base.valid.HexValid;
import com.yk.base.valid.TaskFormValid;
import com.yk.base.valid.TaskTypeValid;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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

    /**
     * NotBlank不设置groups, 在给@Validated指定groups后, 该校验将不生效, 需要采取以下两种方式的任一种
     * 1. 若要使得不设置groups的注释/自定义注释生效, 给@Validated同时指定Default.class
     * 2. 若要使得不设置groups的注释/自定义注释生效, 给@Validated指定的某个groups接口继承javax.validation.groups.Default
     */
    @NotBlank
    private String demo;
}
