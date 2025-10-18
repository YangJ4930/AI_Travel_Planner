package com.travelplanner.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCodeEnum {

    SUCCESS(0, "成功"),
    SYSTEM_ERROR(1, "系统异常"),
    PARAM_ERROR(100, "参数异常"),
    LOGIN_ERROR(300, "登录异常"),
    AUTH_ERROR(400, "权限异常"),
    HTTP_REQUEST_ERROR(4001, "请求外部出错"),
    DATA_SOURCE_NOT_FOUND(4002, "数据源不存在");


    private final int code;

    private final String desc;

}
