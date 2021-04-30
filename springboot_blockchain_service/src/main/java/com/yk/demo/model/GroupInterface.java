package com.yk.demo.model;

import javax.validation.GroupSequence;

/**
 * GroupInterface
 */
public interface GroupInterface
{
    
    interface ITheBrain
    {
    }
    
    interface ITheDetail
    {
    }
    
    @GroupSequence({ITheBrain.class, ITheDetail.class})
    interface ITheAll
    {
    }
}
