package com.yk.demo.model;

import javax.validation.GroupSequence;

public class GroupInterface
{
    public interface ITheBrain
    {
    }
    
    public interface ITheDetail
    {
    }
    
    @GroupSequence({ITheBrain.class, ITheDetail.class})
    public interface ITheAll
    {
    }
}
