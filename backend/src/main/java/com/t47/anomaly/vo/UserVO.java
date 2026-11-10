package com.t47.anomaly.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.t47.anomaly.entity.SysRole;
import com.t47.anomaly.entity.SysUser;

import java.time.LocalDateTime;

/**
 * 用户出参。绝不包含 password 字段。
 */
public record UserVO(
        Long id,
        String username,
        String realName,
        String phone,
        Long roleId,
        String roleCode,
        String roleName,
        Integer status,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime createdAt
) {

    public static UserVO from(SysUser user, SysRole role) {
        return new UserVO(
                user.getId(),
                user.getUsername(),
                user.getRealName(),
                user.getPhone(),
                user.getRoleId(),
                role == null ? null : role.getCode(),
                role == null ? null : role.getName(),
                user.getStatus(),
                user.getCreatedAt());
    }
}
