package com.yk.bitcoin.model;

import lombok.Data;

@Data
public class Key
{
    public Key(String privateKey, String publicKey)
    {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    private String privateKey;

    private String publicKey;
}
