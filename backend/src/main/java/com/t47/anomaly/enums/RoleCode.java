package com.t47.anomaly.enums;

/**
 * 角色编码，与数据库 sys_role.code 保持一致。
 *
 * <p>为什么用枚举而不是散落的字符串：编译期就能发现拼写错误，
 * 且拦截器做角色判断时可以直接引用。</p>
 */
public enum RoleCode {

    /** 管理员：用户、角色管理，拥有全部权限 */
    ADMIN,

    /** 审核员：异常核实、订单处理、统计查看 */
    AUDITOR,

    /** 下单客户：只能操作自己的订单 */
    CUSTOMER
}
