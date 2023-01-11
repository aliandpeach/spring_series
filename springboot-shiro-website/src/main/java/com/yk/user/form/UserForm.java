package com.yk.user.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class UserForm implements Serializable
{
    private static final long serialVersionUID = 5494317014110577880L;

    @NotBlank(message = "用户名或密码为空")
    private String username;

    @NotBlank(message = "用户名或密码为空")
    private String passwd;
}
