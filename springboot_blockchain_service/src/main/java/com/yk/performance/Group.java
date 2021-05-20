package com.yk.performance;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Group
{
    private String name;

    private long useTime;

    private long start;

    private long end;

    private List<Long> groupEachTime = new ArrayList<>();
}
