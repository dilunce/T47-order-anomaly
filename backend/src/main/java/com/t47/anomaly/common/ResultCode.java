package com.t47.anomaly.common;

import lombok.Getter;

/**
 * 统一错误码。
 *
 * <p>约定：
 * <ul>
 *   <li>0 表示成功；</li>
 *   <li>4xx 表示客户端错误（参数、鉴权、越权、资源不存在）；</li>
 *   <li>1000+ 表示业务规则错误（如非法状态流转）；</li>
 *   <li>500 表示系统异常。</li>
 * </ul>
 */
@Getter
public enum ResultCode {

    SUCCESS(0, "成功"),

    PARAM_ERROR(400, "参数校验失败"),
    UNAUTHORIZED(401, "未登录或 Token 已过期"),
    FORBIDDEN(403, "无权访问该资源"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    CONFLICT(409, "数据冲突或重复提交"),

    BIZ_ERROR(1000, "业务处理失败"),
    ILLEGAL_STATUS(1001, "当前状态不允许该操作"),
    DUPLICATE_SUBMIT(1002, "请求重复提交"),

    SYSTEM_ERROR(500, "系统繁忙，请稍后重试");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
