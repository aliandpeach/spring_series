package com.yk.demo.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * BlockchainModel
 */

@Data
public class BlockchainModel
{
    @NotNull(groups = GroupInterface.ITheBrain.class)
    private String phrase;
    
    @NotNull(groups = GroupInterface.ITheDetail.class)
    private String key;
}
