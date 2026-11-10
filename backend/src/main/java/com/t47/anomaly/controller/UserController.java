package com.t47.anomaly.controller;

import com.t47.anomaly.auth.RequireRole;
import com.t47.anomaly.common.PageResult;
import com.t47.anomaly.common.Result;
import com.t47.anomaly.dto.UserRoleDTO;
import com.t47.anomaly.dto.UserStatusDTO;
import com.t47.anomaly.enums.RoleCode;
import com.t47.anomaly.service.UserService;
import com.t47.anomaly.vo.RoleVO;
import com.t47.anomaly.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户管理接口，仅管理员可访问（类级 {@link RequireRole}）。
 */
@Tag(name = "02-用户管理")
@Validated
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@RequireRole(RoleCode.ADMIN)
public class UserController {

    private final UserService userService;

    @Operation(summary = "用户分页查询", description = "支持关键字（账号/姓名）与角色筛选")
    @GetMapping("/admin/users")
    public Result<PageResult<UserVO>> page(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码从 1 开始") long current,
            @RequestParam(defaultValue = "10") @Min(1) @Max(value = 100, message = "每页最多 100 条") long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long roleId) {
        return Result.success(userService.page(current, size, keyword, roleId));
    }

    @Operation(summary = "启用/禁用账号")
    @PatchMapping("/admin/users/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id,
                                     @Valid @RequestBody UserStatusDTO dto) {
        userService.updateStatus(id, dto.getStatus());
        return Result.success();
    }

    @Operation(summary = "调整用户角色")
    @PatchMapping("/admin/users/{id}/role")
    public Result<Void> updateRole(@PathVariable Long id,
                                   @Valid @RequestBody UserRoleDTO dto) {
        userService.updateRole(id, dto.getRoleId());
        return Result.success();
    }

    @Operation(summary = "角色列表", description = "用于管理页面的角色下拉框")
    @GetMapping("/roles")
    public Result<List<RoleVO>> roles() {
        return Result.success(userService.listRoles());
    }
}
