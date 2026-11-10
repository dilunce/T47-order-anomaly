package com.t47.anomaly.auth;

import com.t47.anomaly.common.BizException;
import com.t47.anomaly.common.ResultCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具：签发与解析。
 *
 * <p>JWT 由三部分组成：Header（算法）、Payload（用户信息与过期时间）、Signature（签名）。
 * 签名保证 Payload 不能被篡改——改一个字符签名就对不上，服务端校验直接失败。</p>
 *
 * <p>密钥通过 {@code JWT_SECRET} 环境变量注入，长度不足 32 字节直接启动失败，
 * 避免有人图省事写一个 "123456" 当密钥。</p>
 */
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expireMillis;

    public JwtUtil(@Value("${t47.jwt.secret}") String secret,
                   @Value("${t47.jwt.expire-minutes:120}") long expireMinutes) {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("JWT 密钥长度必须不少于 32 字节，请通过 JWT_SECRET 环境变量注入");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expireMillis = expireMinutes * 60_000L;
    }

    /** 签发 Token：把 userId/username/role 写进 payload */
    public String generate(LoginUser loginUser) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(loginUser.userId()))
                .claim("username", loginUser.username())
                .claim("role", loginUser.roleCode())
                .claim("realName", loginUser.realName())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expireMillis))
                .signWith(secretKey)
                .compact();
    }

    /**
     * 解析 Token，失败统一抛 401。
     *
     * <p>过期、签名错误、格式错误在 jjwt 中都会抛 JwtException，这里统一转换，
     * 避免把底层异常细节暴露给前端。</p>
     */
    public LoginUser parse(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return new LoginUser(
                    Long.valueOf(claims.getSubject()),
                    claims.get("username", String.class),
                    claims.get("role", String.class),
                    claims.get("realName", String.class));
        } catch (JwtException | IllegalArgumentException e) {
            throw new BizException(ResultCode.UNAUTHORIZED, "Token 无效或已过期");
        }
    }

    /** 有效期（秒），登录接口返回给前端用于倒计时/自动续期 */
    public long getExpireSeconds() {
        return expireMillis / 1000;
    }
}
