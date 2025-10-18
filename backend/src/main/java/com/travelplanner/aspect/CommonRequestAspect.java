package com.travelplanner.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.travelplanner.aspect.annotation.LoginCheck;

import com.travelplanner.common.enums.ResponseCodeEnum;
import com.travelplanner.common.enums.TravelPlannerException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.Optional;

/**
 * HTTP请求统一处理切面
 * 负责处理所有HTTP请求，包括：
 * 1. 请求日志记录
 * 2. 权限验证
 * 3. 响应日志记录
 */
@Aspect
@Component
@Slf4j
public class CommonRequestAspect {

    /**
     * 定义控制器切点，拦截所有controller包下的方法
     */
    @Pointcut("execution(* com.travelplanner.controller..*.*(..))")
    public void controllerPointcut() {
    }

    /**
     * 请求处理的环绕通知
     * 按顺序执行：请求日志记录 -> 权限校验 -> 原方法执行 -> 响应日志记录
     *
     * @param joinPoint 切点信息
     * @return 原方法的执行结果
     * @throws Throwable 执行过程中可能抛出的异常
     */
    @Around("controllerPointcut()")
    public Object commonRequestAspect(ProceedingJoinPoint joinPoint) throws Throwable {
        // 记录请求日志
        logRequest(joinPoint);
        // 登录校验
        checkLogin(joinPoint);
        // 执行原方法
        Object result = joinPoint.proceed();
        // 记录响应日志
        logResponse(joinPoint, result);
        return result;
    }


    /**
     * 记录请求日志
     * 包含：请求URI、调用方法、请求参数等信息
     *
     * @param joinPoint 切点信息
     */
    private void logRequest(ProceedingJoinPoint joinPoint) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                log.info("接收到http请求, URL:{}, Method:{}#{}, param:{}",
                        request.getRequestURI(),
                        signature.getDeclaringType().getSimpleName(),
                        signature.getName(),
                        getRequestParamInfo(joinPoint, signature));
            }
        } catch (Exception e) {
            log.error("记录请求日志失败", e);
        }
    }

    /**
     * 获取请求参数信息
     * 格式化参数名和参数值，便于日志查看
     *
     * @param joinPoint 切点信息
     * @param signature 方法签名
     * @return 格式化的参数信息字符串
     */
    private String getRequestParamInfo(ProceedingJoinPoint joinPoint, MethodSignature signature) {
        Object[] args = joinPoint.getArgs();
        Parameter[] parameters = signature.getMethod().getParameters();
        if (args == null || args.length == 0) {
            return "无参数";
        }
        StringBuilder paramInfo = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                paramInfo.append(", ");
            }
            String paramName = parameters[i].getName();
            Object paramValue = args[i];
            paramInfo.append(paramName).append("=").append(paramValue);
        }
        return paramInfo.toString();
    }

    /**
     * 记录响应日志
     * 包含：请求URI、调用方法、响应结果等信息
     *
     * @param joinPoint 切点信息
     * @param result    方法执行结果
     */
    private void logResponse(ProceedingJoinPoint joinPoint, Object result) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                log.info("返回http响应, URL:{}, Method:{}#{}, result:{}",
                        request.getRequestURI(),
                        signature.getDeclaringType().getSimpleName(),
                        signature.getName(),
                        result);
            }
        } catch (Exception e) {
            log.error("记录响应日志失败", e);
        }
    }


    /**
     * 执行登录校验
     *
     * @param joinPoint 切点信息
     */
    private void checkLogin(ProceedingJoinPoint joinPoint) {
        try {
            // 获取AuthorityCheck注解(优先获取方法注解，其次获取类注解)
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Class<?> targetClass = method.getDeclaringClass();
            LoginCheck loginCheck = Optional.ofNullable(method.getAnnotation(LoginCheck.class)).orElse(targetClass.getAnnotation(LoginCheck.class));
            if (Objects.nonNull(loginCheck) && !loginCheck.allowAnonymous()) {
                StpUtil.checkLogin();
            }
        } catch (Exception e) {
            log.error("登录校验过程中发生错误", e);
            throw new TravelPlannerException(ResponseCodeEnum.LOGIN_ERROR, "登录校验异常");
        }
    }
}
