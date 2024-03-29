package com.yk.base.valid;

import javax.validation.GroupSequence;

public class GroupConstant
{
    // 按照顺序验证
    @GroupSequence({A.class, B.class, C.class})
    public interface SequentialCombination1
    {
    }

    @GroupSequence({C.class, D.class})
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
    public interface D
    {

    }
}
