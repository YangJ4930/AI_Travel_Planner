package com.travelplanner.common.enums;

import com.travelplanner.common.enums.ResponseCodeEnum;
import lombok.Getter;

@Getter
public class TravelPlannerException extends RuntimeException {

    /**
     * 异常码
     */
    private final ResponseCodeEnum errorCode;

    /**
     * 异常说明
     */
    private final String message;

    public TravelPlannerException(ResponseCodeEnum code, String message) {
        this.errorCode = code;
        this.message = message;
    }
}
