package com.travelplanner.aspect.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 登录校验注解
 * 用于方法或类级别的权限控制
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginCheck {

    /**
     * 是否允许匿名(未登录)访问
     * @return 默认为false,表示默认需要登录
     */
    boolean allowAnonymous() default false;
}
