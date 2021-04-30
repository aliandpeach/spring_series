package com.yk.demo.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * BlockchainModel
 */

@Data
public class BlockchainModel
{
    @NotNull(message = "phrase不能为空", groups = {GroupInterface.ITheBrain.class})
    private String phrase;
    
    @NotNull(message = "key不能为空", groups = {GroupInterface.ITheDetail.class})
    private String key;
}