package com.t47.anomaly.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码编码器配置。
 *
 * <p>为什么用 BCrypt 而不是 MD5：
 * <ul>
 *   <li>BCrypt 自带随机盐，同样的密码每次加密结果都不同，
 *       攻击者无法用彩虹表批量破解；</li>
 *   <li>MD5 计算太快，GPU 每秒可跑几十亿次，密码一旦泄露基本秒破；</li>
 *   <li>BCrypt 的 cost 参数可以故意放慢计算速度（默认 10，约 100ms/次）。</li>
 * </ul>
 *
 * <p>这里只引入 spring-security-crypto 的 BCrypt 实现，没有引入完整 Spring Security，
 * 认证逻辑仍然是我们自己的拦截器，便于答辩讲清楚原理。</p>
 */
@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
