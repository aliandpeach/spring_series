package com.yk.demo.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class DemoModel implements Serializable {
    private String id;
    private String name;

    public DemoModel() {
    }

    public DemoModel(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
