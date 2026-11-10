package com.t47.anomaly.common;

import lombok.Getter;

/**
 * 业务异常：表示"代码逻辑没问题，是业务规则不允许"。
 *
 * <p>由全局异常处理器捕获，返回 HTTP 200 + 业务码，
 * 与 NPE、SQL 异常这类系统异常区分开。</p>
 */
@Getter
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final int code;

    public BizException(String message) {
        super(message);
        this.code = ResultCode.BIZ_ERROR.getCode();
    }

    public BizException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public BizException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }
}
