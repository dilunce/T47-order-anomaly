package com.t47.anomaly.vo;

import com.t47.anomaly.entity.SysRole;

/**
 * 角色出参。
 */
public record RoleVO(Long id, String code, String name, String description) {

    public static RoleVO from(SysRole role) {
        return new RoleVO(role.getId(), role.getCode(), role.getName(), role.getDescription());
    }
}
