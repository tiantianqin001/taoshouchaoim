package com.pingmo.chengyan.activity.shop.common;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 资源结果包装类，此类反应资源获取的状态和结果
 *
 * @param <T>
 */
public class Resource<T> {
    @NonNull
    public final Status status;

    @Nullable
    public final String message;

    public final int code;

    @Nullable
    public final T data;

    public Resource(@NonNull Status status, @Nullable T data, @Nullable int code) {
        this.status = status;
        this.data = data;
        this.code = code;
        this.message = ErrorCode.fromCode(code).getMessage();
    }

    public Resource(@NonNull Status status, @Nullable T data, @Nullable int code, String message) {
        this.status = status;
        this.data = data;
        this.code = code;

        String message1 = ErrorCode.fromCode(code).getMessage();
        if (!TextUtils.isEmpty(message1)) {
            this.message = message1;
        } else {
            this.message = message;
        }
    }

    public static <T> Resource<T> success(@Nullable T data) {
        return new Resource<>(Status.SUCCESS, data, ErrorCode.NONE_ERROR.getCode());
    }

    public static <T> Resource<T> error(int code, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, code);
    }

    public static <T> Resource<T> error(int code, @Nullable T data,String message) {
        return new Resource<>(Status.ERROR, data, code,message);
    }

    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(Status.LOADING, data, ErrorCode.NONE_ERROR.getCode());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Resource<?> resource = (Resource<?>) o;

        if (status != resource.status) {
            return false;
        }
        if (message != null ? !message.equals(resource.message) : resource.message != null) {
            return false;
        }
        return data != null ? data.equals(resource.data) : resource.data == null;
    }

    @Override
    public int hashCode() {
        int result = status.hashCode();
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
