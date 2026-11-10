package com.t47.anomaly.controller;

import com.t47.anomaly.common.Result;
import com.t47.anomaly.dto.LoginDTO;
import com.t47.anomaly.dto.RegisterDTO;
import com.t47.anomaly.service.AuthService;
import com.t47.anomaly.vo.LoginVO;
import com.t47.anomaly.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口。
 *
 * <p>{@code /login} 与 {@code /register} 在拦截器白名单中，不需要 Token；
 * {@code /me} 需要携带 Token。</p>
 */
@Tag(name = "01-认证")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "登录", description = "账号密码登录，成功返回 JWT Token")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(authService.login(dto));
    }

    @Operation(summary = "注册", description = "注册后角色固定为 CUSTOMER")
    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody RegisterDTO dto) {
        return Result.success(authService.register(dto));
    }

    @Operation(summary = "当前登录用户", description = "根据 Token 返回当前用户信息")
    @GetMapping("/me")
    public Result<UserVO> me() {
        return Result.success(authService.currentUser());
    }
}
