package com.t47.anomaly.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 修改用户角色入参。
 */
@Data
public class UserRoleDTO {

    @NotNull(message = "角色ID不能为空")
    private Long roleId;
}
