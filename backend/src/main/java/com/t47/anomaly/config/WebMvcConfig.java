package com.t47.anomaly.config;

import com.t47.anomaly.auth.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置：注册 JWT 拦截器并声明白名单。
 *
 * <p>白名单原则：只放行"登录前必须能访问"的接口。
 * 新增免登录接口时，必须确认它不会泄漏业务数据。</p>
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/login",      // 登录
                        "/api/auth/register",   // 注册
                        "/api/health"           // 容器探针
                );
    }
}
