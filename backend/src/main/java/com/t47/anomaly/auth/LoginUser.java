package com.t47.anomaly.auth;

/**
 * 登录用户信息，只放 JWT 里携带的最小字段，不包含密码等敏感信息。
 *
 * @param userId   用户ID
 * @param username 登录账号
 * @param roleCode 角色编码（ADMIN/AUDITOR/CUSTOMER）
 * @param realName 姓名
 */
public record LoginUser(Long userId, String username, String roleCode, String realName) {
}
