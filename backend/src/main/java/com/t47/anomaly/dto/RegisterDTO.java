package com.t47.anomaly.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 注册入参。
 *
 * <p>注册只能创建 CUSTOMER 角色，角色提升必须由管理员操作，
 * 避免有人注册时自己传 roleId=1 变成管理员。</p>
 */
@Data
public class RegisterDTO {

    @NotBlank(message = "账号不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "账号需为 4-20 位字母、数字或下划线")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度需为 6-32 位")
    private String password;

    @NotBlank(message = "姓名不能为空")
    @Size(max = 64, message = "姓名过长")
    private String realName;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
}
