package com.example.demo.common.result;

/**
 * 状态码枚举
 */
public enum ResultCode implements IResultCode {

    // 基本信息
    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),

    // 4xx 客户端错误
    BAD_REQUEST(400, "请求错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),

    // 5xx 服务器错误
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),

    // 业务错误码
    PARAM_VALIDATE_FAILED(1001, "参数校验失败"),

    // 用户相关错误码
    USER_NOT_EXIST(1102, "用户不存在"),
    USER_ALREADY_EXIST(1103, "用户已存在"),
    USER_PASSWORD_ERROR(1104, "密码错误"),
    USER_DISABLED(1105, "用户已禁用"),

    // 鉴权相关错误码
    TOKEN_EXPIRED(1201, "Token已过期"),
    TOKEN_INVALID(1202, "Token无效");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

/**
 * 状态码接口
 */
interface IResultCode {
    Integer getCode();
    String getMessage();
}
