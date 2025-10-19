package com.travelplanner.config;

import com.travelplanner.common.Response;
import com.travelplanner.common.enums.ResponseCodeEnum;
import com.travelplanner.common.enums.TravelPlannerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(TravelPlannerException.class)
    public Response<Void> handleUniMinerException(TravelPlannerException e) {
        log.error("http请求业务异常: {}", e.getMessage(), e);
        return Response.fail(e.getErrorCode(), e.getMessage());
    }

    /**
     * 处理其他所有异常
     */
    @ExceptionHandler(Exception.class)
    public Response<Void> handleException(Exception e) {
        log.error("http请求系统异常", e);
        return Response.fail(ResponseCodeEnum.SYSTEM_ERROR, "系统异常，请联系管理员");
    }
}
