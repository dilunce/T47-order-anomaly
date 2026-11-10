package com.t47.anomaly.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常处理。
 *
 * <p>两条原则：
 * <ol>
 *   <li>前端永远只看到统一 {@link Result} 结构，不出现 Tomcat 默认错误页；</li>
 *   <li>HTTP 状态码与语义一致：400 参数错、401 未登录、403 越权、404 不存在、409 冲突、500 系统异常。
 *       这样前端拦截器可以只按 HTTP 状态码统一处理登录过期等问题。</li>
 * </ol>
 *
 * <p>安全：日志只记录业务码与提示，不打印密码、Token、完整请求体；系统异常虽打印堆栈，
 * 但返回给前端的只有一句"系统繁忙"。</p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 业务异常：预期内的失败，用 warn 级别，不打堆栈 */
    @ExceptionHandler(BizException.class)
    public ResponseEntity<Result<Void>> handleBizException(BizException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return ResponseEntity.status(toHttpStatus(e.getCode()))
                .body(Result.fail(e.getCode(), e.getMessage()));
    }

    /** @RequestBody + @Valid 校验失败 → 400 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败: {}", message);
        return ResponseEntity.badRequest().body(Result.fail(ResultCode.PARAM_ERROR, message));
    }

    /** 表单/查询参数绑定校验失败 → 400 */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<Void>> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数绑定失败: {}", message);
        return ResponseEntity.badRequest().body(Result.fail(ResultCode.PARAM_ERROR, message));
    }

    /** @RequestParam / @PathVariable 上的约束校验失败 → 400 */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<Void>> handleConstraintViolation(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(v -> v.getMessage())
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败: {}", message);
        return ResponseEntity.badRequest().body(Result.fail(ResultCode.PARAM_ERROR, message));
    }

    /** 请求体 JSON 格式错误或日期格式不匹配 → 400 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Result<Void>> handleNotReadable(HttpMessageNotReadableException e) {
        log.warn("请求体解析失败: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(Result.fail(ResultCode.PARAM_ERROR, "请求体格式错误，请检查 JSON 或日期格式"));
    }

    /** 唯一索引冲突：重复提交 / 订单号重复 → 409 */
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Result<Void>> handleDuplicateKey(DuplicateKeyException e) {
        log.warn("唯一约束冲突: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Result.fail(ResultCode.CONFLICT, "数据已存在，请勿重复提交"));
    }

    /** 兜底：系统异常 → 500，不向前端暴露内部信息 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e) {
        log.error("系统异常", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.fail(ResultCode.SYSTEM_ERROR));
    }

    /**
     * 业务码 → HTTP 状态码的映射。
     * 1001/1002 属于"与当前状态冲突"，用 409 比 400 更准确。
     */
    private HttpStatus toHttpStatus(int code) {
        return switch (code) {
            case 400 -> HttpStatus.BAD_REQUEST;
            case 401 -> HttpStatus.UNAUTHORIZED;
            case 403 -> HttpStatus.FORBIDDEN;
            case 404 -> HttpStatus.NOT_FOUND;
            case 405 -> HttpStatus.METHOD_NOT_ALLOWED;
            case 409, 1001, 1002 -> HttpStatus.CONFLICT;
            case 500 -> HttpStatus.INTERNAL_SERVER_ERROR;
            default -> HttpStatus.BAD_REQUEST;
        };
    }
}
