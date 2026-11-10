package com.t47.anomaly.auth;

import com.t47.anomaly.enums.RoleCode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口角色限制注解。
 *
 * <p>可以标在方法上，也可以标在 Controller 类上（类上表示该 Controller 所有接口都受限）。
 * 由 {@link JwtInterceptor} 在执行 Controller 之前校验。</p>
 *
 * <pre>{@code
 * @RequireRole(RoleCode.ADMIN)
 * @PatchMapping("/users/{id}/status")
 * }</pre>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {

    /** 允许访问的角色，满足其中一个即可 */
    RoleCode[] value();
}
