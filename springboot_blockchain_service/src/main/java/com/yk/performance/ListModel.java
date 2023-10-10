package com.yk.performance;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

/**
 * ArrayModel
 */
@Data
public class ListModel
{
    @NotEmpty
    private List<Map<String, String>> images;
}