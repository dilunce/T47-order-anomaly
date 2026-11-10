package com.t47.anomaly.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 修改用户状态入参。
 */
@Data
public class UserStatusDTO {

    /** 1 正常，0 禁用 */
    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态只能是 0 或 1")
    @Max(value = 1, message = "状态只能是 0 或 1")
    private Integer status;
}
