package com.t47.anomaly.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录入参。
 */
@Data
public class LoginDTO {

    @NotBlank(message = "账号不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
