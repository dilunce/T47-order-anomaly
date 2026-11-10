package com.t47.anomaly.controller;

import com.t47.anomaly.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 健康检查接口，供容器探针（Docker HEALTHCHECK）和运维监控使用。
 *
 * <p>该接口在拦截器白名单中放行，不需要 Token。</p>
 */
@Tag(name = "系统健康检查")
@RestController
@RequestMapping("/api")
public class HealthController {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Value("${spring.application.name}")
    private String appName;

    @Operation(summary = "健康检查", description = "返回服务状态，供容器探针与监控使用")
    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("status", "UP");
        data.put("service", appName);
        data.put("time", LocalDateTime.now().format(FORMATTER));
        return Result.success(data);
    }
}
