package com.t47.anomaly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * T47 AI 订单异常识别助手 - 启动类。
 *
 * <p>注意：Mapper 接口统一使用 {@code @Mapper} 注解，暂不使用 @MapperScan，
 * 等 mapper 包建立后再按需加回。</p>
 */
@SpringBootApplication
public class T47Application {

    public static void main(String[] args) {
        SpringApplication.run(T47Application.class, args);
    }
}
