package com.t47.anomaly.service;

import com.t47.anomaly.common.PageResult;
import com.t47.anomaly.vo.RoleVO;
import com.t47.anomaly.vo.UserVO;

import java.util.List;

/**
 * 用户管理服务（仅管理员可调用）。
 */
public interface UserService {

    /** 多条件分页查询用户 */
    PageResult<UserVO> page(long current, long size, String keyword, Long roleId);

    /** 启用/禁用账号 */
    void updateStatus(Long id, Integer status);

    /** 调整角色 */
    void updateRole(Long id, Long roleId);

    /** 角色下拉列表 */
    List<RoleVO> listRoles();
}
