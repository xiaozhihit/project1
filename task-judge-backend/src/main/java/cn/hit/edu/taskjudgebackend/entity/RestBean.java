package cn.hit.edu.taskjudgebackend.entity;

import lombok.Data;

/**
 * 用于返回响应结果
 * @param <T>
 */
@Data
public class RestBean<T> {
    private int status;//状态码
    private boolean success;
    private T message;//data

    private RestBean(int status, boolean success, T message) {
        this.status = status;
        this.success = success;
        this.message = message;
    }

    public static <T> RestBean<T> success() {
        return new RestBean<T>(200,true,null);
    }

    public static <T> RestBean<T> success(T data) {
        return new RestBean<T>(200,true,data);
    }

    public static <T> RestBean<T> failure(int status) {
        return new RestBean<T>(status,false,null);
    }

    public static <T> RestBean<T> failure(int status ,T data) {
        return new RestBean<T>(status,false,data);
    }
}
