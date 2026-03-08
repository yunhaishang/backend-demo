package com.example.demo.common.result;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 统一返回结果
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 消息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    /**
     * 时间戳
     */
    private LocalDateTime timestamp;

    /**
     * 成功（无数据）
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 成功（有数据）
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        result.setData(data);
        result.setTimestamp(LocalDateTime.now());
        return result;
    }

    /**
     * 失败
     */
    public static <T> Result<T> error() {
        return error(ResultCode.FAILED);
    }

    /**
     * 失败（自定义消息）
     */
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.FAILED.getCode());
        result.setMessage(message);
        result.setTimestamp(LocalDateTime.now());
        return result;
    }

    /**
     * 失败（状态码+消息）
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setTimestamp(LocalDateTime.now());
        return result;
    }

    /**
     * 失败（枚举）
     */
    public static <T> Result<T> error(IResultCode resultCode) {
        Result<T> result = new Result<>();
        result.setCode(resultCode.getCode());
        result.setMessage(resultCode.getMessage());
        result.setTimestamp(LocalDateTime.now());
        return result;
    }
}
