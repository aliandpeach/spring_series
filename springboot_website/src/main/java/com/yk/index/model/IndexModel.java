package com.yk.index.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndexModel
{
    private String name;
    private String id;
    private boolean responseContentType;
}
