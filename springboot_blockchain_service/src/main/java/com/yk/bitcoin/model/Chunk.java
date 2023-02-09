package com.yk.bitcoin.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class Chunk
{
    @Getter
    @Setter
    private List<Key> dataList = new ArrayList<>();
}
