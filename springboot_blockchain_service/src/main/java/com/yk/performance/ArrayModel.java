package com.yk.performance;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * ArrayModel
 */
@Data
public class ArrayModel
{
    @NotEmpty
    private List<String> images;

    @NotEmpty
    private String[] urls;
}
