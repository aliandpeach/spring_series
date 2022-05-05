package com.yk.test.activemq.topic;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2022/05/05 16:31:43
 */
@Data
@AllArgsConstructor
public class User implements Serializable
{
    private static final long serialVersionUID = 3409967961884982729L;

    private String name;

    private String role;

    @Override
    public String toString()
    {
        return "User{" +
                "name='" + name + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
