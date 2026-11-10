package com.t47.anomaly.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.t47.anomaly.common.BizException;
import com.t47.anomaly.common.Result;
import com.t47.anomaly.common.ResultCode;
import com.t47.anomaly.enums.RoleCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Arrays;

/**
 * JWT 拦截器：登录校验 + 角色校验。
 *
 * <p>执行流程：
 * <ol>
 *   <li>放行非 Controller 请求（静态资源等）；</li>
 *   <li>取 {@code Authorization: Bearer xxx}，缺失或解析失败 → 401；</li>
 *   <li>解析成功写入 {@link CurrentUser}；</li>
 *   <li>检查 {@link RequireRole}，角色不匹配 → 403；</li>
 *   <li>请求结束后 afterCompletion 清理 ThreadLocal。</li>
 * </ol>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    public static final String HEADER = "Authorization";
    public static final String PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // 非 Controller 方法（静态资源、favicon 等）直接放行
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        String header = request.getHeader(HEADER);
        if (header == null || !header.startsWith(PREFIX)) {
            writeJson(response, HttpServletResponse.SC_UNAUTHORIZED,
                    Result.fail(ResultCode.UNAUTHORIZED, "请先登录"));
            return false;
        }

        LoginUser loginUser;
        try {
            loginUser = jwtUtil.parse(header.substring(PREFIX.length()).trim());
        } catch (BizException e) {
            writeJson(response, HttpServletResponse.SC_UNAUTHORIZED,
                    Result.fail(e.getCode(), e.getMessage()));
            return false;
        }

        CurrentUser.set(loginUser);

        // 角色校验：方法上的注解优先，其次看类上的注解
        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        if (requireRole == null) {
            requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
        }
        if (requireRole != null) {
            RoleCode[] allowed = requireRole.value();
            boolean matched = Arrays.stream(allowed)
                    .anyMatch(role -> role.name().equals(loginUser.roleCode()));
            if (!matched) {
                CurrentUser.clear();
                log.warn("越权访问被拒绝: userId={}, role={}, uri={}",
                        loginUser.userId(), loginUser.roleCode(), request.getRequestURI());
                writeJson(response, HttpServletResponse.SC_FORBIDDEN,
                        Result.fail(ResultCode.FORBIDDEN));
                return false;
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        // 必须清理：Tomcat 线程会被复用，不清理会串号（上一个用户的身份被下一个请求读到）
        CurrentUser.clear();
    }

    private void writeJson(HttpServletResponse response, int status, Result<?> body) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
