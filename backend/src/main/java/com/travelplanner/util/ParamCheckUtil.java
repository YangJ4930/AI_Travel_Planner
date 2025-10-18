package com.travelplanner.util;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.travelplanner.common.enums.ResponseCodeEnum;
import com.travelplanner.common.enums.TravelPlannerException;

import java.util.Collection;
import java.util.Objects;

public class ParamCheckUtil {

    public static void notNull(Object object, String message) {
        if (Objects.isNull(object)) {
            throw new TravelPlannerException(ResponseCodeEnum.PARAM_ERROR, message);
        }
    }

    public static void isNull(Object object, String message) {
        if (Objects.nonNull(object)) {
            throw new TravelPlannerException(ResponseCodeEnum.PARAM_ERROR, message);
        }
    }

    public static void notEmpty(String str, String message) {
        if (StringUtils.isEmpty(str)) {
            throw new TravelPlannerException(ResponseCodeEnum.PARAM_ERROR, message);
        }
    }

    public static void isEmpty(String str, String message) {
        if (StringUtils.isNotEmpty(str)) {
            throw new TravelPlannerException(ResponseCodeEnum.PARAM_ERROR, message);
        }
    }

    public static void notBlank(String str, String message) {
        if (StringUtils.isBlank(str)) {
            throw new TravelPlannerException(ResponseCodeEnum.PARAM_ERROR, message);
        }
    }

    public static void isBlank(String str, String message) {
        if (StringUtils.isNotBlank(str)) {
            throw new TravelPlannerException(ResponseCodeEnum.PARAM_ERROR, message);
        }
    }

    public static void notEmpty(Collection<?> collection, String message) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new TravelPlannerException(ResponseCodeEnum.PARAM_ERROR, message);
        }
    }

    public static void isEmpty(Collection<?> collection, String message) {
        if (!CollectionUtils.isEmpty(collection)) {
            throw new TravelPlannerException(ResponseCodeEnum.PARAM_ERROR, message);
        }
    }

}