package com.t47.anomaly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.t47.anomaly.auth.CurrentUser;
import com.t47.anomaly.auth.JwtUtil;
import com.t47.anomaly.auth.LoginUser;
import com.t47.anomaly.common.BizException;
import com.t47.anomaly.common.ResultCode;
import com.t47.anomaly.dto.LoginDTO;
import com.t47.anomaly.dto.RegisterDTO;
import com.t47.anomaly.entity.SysRole;
import com.t47.anomaly.entity.SysUser;
import com.t47.anomaly.enums.RoleCode;
import com.t47.anomaly.mapper.SysRoleMapper;
import com.t47.anomaly.mapper.SysUserMapper;
import com.t47.anomaly.service.AuthService;
import com.t47.anomaly.vo.LoginVO;
import com.t47.anomaly.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public LoginVO login(LoginDTO dto) {
        SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, dto.getUsername()));

        // 账号不存在与密码错误返回同一句提示，避免攻击者通过接口"试"出哪些账号存在
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            log.warn("登录失败: username={}", dto.getUsername());
            throw new BizException(ResultCode.UNAUTHORIZED, "账号或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BizException(ResultCode.FORBIDDEN, "账号已被禁用，请联系管理员");
        }

        SysRole role = roleMapper.selectById(user.getRoleId());
        if (role == null) {
            throw new BizException(ResultCode.BIZ_ERROR, "账号角色配置异常，请联系管理员");
        }

        LoginUser loginUser = new LoginUser(user.getId(), user.getUsername(),
                role.getCode(), user.getRealName());
        String token = jwtUtil.generate(loginUser);

        // 日志只记录必要信息：绝不打印密码或完整 Token
        log.info("登录成功: userId={}, username={}, role={}",
                user.getId(), user.getUsername(), role.getCode());

        return LoginVO.of(token, jwtUtil.getExpireSeconds(), UserVO.from(user, role));
    }

    @Override
    public UserVO register(RegisterDTO dto) {
        Long exists = userMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, dto.getUsername()));
        if (exists != null && exists > 0) {
            throw new BizException(ResultCode.CONFLICT, "账号已存在");
        }

        // 注册固定为 CUSTOMER 角色，防止前端伪造 roleId 注册成管理员
        SysRole customerRole = roleMapper.selectOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getCode, RoleCode.CUSTOMER.name()));
        if (customerRole == null) {
            throw new BizException(ResultCode.SYSTEM_ERROR, "系统角色未初始化");
        }

        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRealName(dto.getRealName());
        user.setPhone(dto.getPhone());
        user.setRoleId(customerRole.getId());
        user.setStatus(1);
        userMapper.insert(user);

        log.info("用户注册成功: userId={}, username={}", user.getId(), user.getUsername());
        return UserVO.from(user, customerRole);
    }

    @Override
    public UserVO currentUser() {
        Long userId = CurrentUser.requireUserId();
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ResultCode.NOT_FOUND, "用户不存在");
        }
        SysRole role = roleMapper.selectById(user.getRoleId());
        return UserVO.from(user, role);
    }
}
