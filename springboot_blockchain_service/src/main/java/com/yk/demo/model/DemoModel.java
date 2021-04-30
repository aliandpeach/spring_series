package com.yk.demo.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * DemoModel
 */

@Data
public class DemoModel
{
    @NotNull
    private Long id;
    
    @NotNull
    private String name;
}
