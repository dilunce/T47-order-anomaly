package com.t47.anomaly.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体，对应 sys_user 表。
 */
@Data
@TableName("sys_user")
public class SysUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    /** BCrypt 哈希，永不返回给前端，也不允许出现在日志里 */
    @JsonIgnore
    private String password;

    private String realName;

    private String phone;

    private Long roleId;

    /** 1 正常，0 禁用 */
    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
