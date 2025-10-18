package com.travelplanner.common;

import com.travelplanner.common.enums.ResponseCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Response<T> implements Serializable {

    private int code;

    private String message;

    private T data;

    public static <T> Response<T> success(T data) {
        return new Response<>(ResponseCodeEnum.SUCCESS.getCode(), null, data);
    }

    public static <T> Response<T> success() {
        return new Response<>(ResponseCodeEnum.SUCCESS.getCode(), null, null);
    }

    public static <T> Response<T> fail(ResponseCodeEnum failEnum, String message) {
        return new Response<>(failEnum.getCode(), message, null);
    }
}