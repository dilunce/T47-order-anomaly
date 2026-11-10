package com.t47.anomaly.vo;

/**
 * 登录出参：Token + 用户信息 + 有效期。
 *
 * <p>前端拿到 token 后存 localStorage，之后每个请求带 {@code Authorization: Bearer <token>}。</p>
 */
public record LoginVO(
        String token,
        String tokenType,
        long expiresIn,
        UserVO user
) {
    public static LoginVO of(String token, long expiresInSeconds, UserVO user) {
        return new LoginVO(token, "Bearer", expiresInSeconds, user);
    }
}
