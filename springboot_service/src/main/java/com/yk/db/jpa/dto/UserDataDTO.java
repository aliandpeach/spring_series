package com.yk.db.jpa.dto;

import com.yk.db.jpa.model.Role;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/11/17 15:40:28
 */
@Data
@NoArgsConstructor
public class UserDataDTO
{
    @ApiModelProperty(position = 0)
    private String id;

    @ApiModelProperty(position = 1)
    private String name;

    @ApiModelProperty(position = 2)
    List<Role> roles;
}
