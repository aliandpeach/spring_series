package com.yk.base.valid;

import javax.validation.GroupSequence;

public class GroupConstant
{
    // 组序列
    @GroupSequence({A.class, B.class, C.class})
    public interface SequentialCombination1
    {
    }

    // 组序列
    @GroupSequence({A.class, B.class})
    public interface SequentialCombination2
    {
    }

    public interface A
    {

    }

    public interface B
    {

    }

    public interface C
    {

    }
}
