package com.t47.anomaly.auth;

import com.t47.anomaly.common.BizException;
import com.t47.anomaly.common.ResultCode;

/**
 * 当前请求的登录用户上下文。
 *
 * <p>拦截器解析 Token 后写入，Controller/Service 直接读取，
 * 避免每个方法都手动传 userId。请求结束必须 clear，防止线程池复用导致串号。</p>
 */
public final class CurrentUser {

    private static final ThreadLocal<LoginUser> HOLDER = new ThreadLocal<>();

    private CurrentUser() {
    }

    public static void set(LoginUser loginUser) {
        HOLDER.set(loginUser);
    }

    public static LoginUser get() {
        return HOLDER.get();
    }

    /** 取当前用户，取不到说明没登录，直接按 401 处理 */
    public static LoginUser require() {
        LoginUser loginUser = HOLDER.get();
        if (loginUser == null) {
            throw new BizException(ResultCode.UNAUTHORIZED);
        }
        return loginUser;
    }

    public static Long requireUserId() {
        return require().userId();
    }

    public static String requireRoleCode() {
        return require().roleCode();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
