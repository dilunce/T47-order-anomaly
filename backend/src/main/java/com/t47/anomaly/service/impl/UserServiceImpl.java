package com.t47.anomaly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.t47.anomaly.auth.CurrentUser;
import com.t47.anomaly.common.BizException;
import com.t47.anomaly.common.PageResult;
import com.t47.anomaly.common.ResultCode;
import com.t47.anomaly.entity.SysRole;
import com.t47.anomaly.entity.SysUser;
import com.t47.anomaly.enums.RoleCode;
import com.t47.anomaly.mapper.SysRoleMapper;
import com.t47.anomaly.mapper.SysUserMapper;
import com.t47.anomaly.service.UserService;
import com.t47.anomaly.vo.RoleVO;
import com.t47.anomaly.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户管理服务实现。
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;

    @Override
    public PageResult<UserVO> page(long current, long size, String keyword, Long roleId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            // 关键字同时匹配账号和姓名，外层 and 保证与 roleId 条件是 AND 关系
            wrapper.and(w -> w.like(SysUser::getUsername, keyword)
                    .or()
                    .like(SysUser::getRealName, keyword));
        }
        if (roleId != null) {
            wrapper.eq(SysUser::getRoleId, roleId);
        }
        wrapper.orderByDesc(SysUser::getId);

        Page<SysUser> page = userMapper.selectPage(new Page<>(current, size), wrapper);

        // 一次性把角色查出来做映射，避免在循环里逐条查角色（N+1 问题）
        Map<Long, SysRole> roleMap = roleMapper.selectList(null).stream()
                .collect(Collectors.toMap(SysRole::getId, Function.identity()));

        List<UserVO> records = page.getRecords().stream()
                .map(user -> UserVO.from(user, roleMap.get(user.getRoleId())))
                .toList();

        return new PageResult<>(page.getTotal(), page.getPages(), page.getCurrent(), page.getSize(), records);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        if (id.equals(CurrentUser.requireUserId())) {
            throw new BizException("不能修改自己的账号状态");
        }
        SysUser user = requireUser(id);
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    public void updateRole(Long id, Long roleId) {
        SysUser user = requireUser(id);
        SysRole role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BizException(ResultCode.NOT_FOUND, "角色不存在");
        }

        // 防止把最后一个管理员降级，导致系统没人能管理
        if (RoleCode.ADMIN.name().equals(roleOf(user).getCode())
                && !RoleCode.ADMIN.name().equals(role.getCode())) {
            Long adminCount = userMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getRoleId, roleOf(user).getId())
                    .eq(SysUser::getStatus, 1));
            if (adminCount != null && adminCount <= 1) {
                throw new BizException("系统至少需要保留一个管理员");
            }
        }

        user.setRoleId(roleId);
        userMapper.updateById(user);
    }

    @Override
    public List<RoleVO> listRoles() {
        return roleMapper.selectList(null).stream().map(RoleVO::from).toList();
    }

    private SysUser requireUser(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BizException(ResultCode.NOT_FOUND, "用户不存在");
        }
        return user;
    }

    private SysRole roleOf(SysUser user) {
        SysRole role = roleMapper.selectById(user.getRoleId());
        if (role == null) {
            throw new BizException(ResultCode.SYSTEM_ERROR, "用户角色数据异常");
        }
        return role;
    }
}
