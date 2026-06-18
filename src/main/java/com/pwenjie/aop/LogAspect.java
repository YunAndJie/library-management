package com.pwenjie.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LogAspect {

    @Around("execution(* com.pwenjie.controller..*.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        Object[] args = joinPoint.getArgs();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String ip = "unknown";
        String requestURI = "unknown";
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            ip = request.getRemoteAddr();
            requestURI = request.getRequestURI();
        }

        long start = System.currentTimeMillis();

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            long elapsed = System.currentTimeMillis() - start;
            log.warn("[{}] {}.{}({}) - {}ms - 异常: {} - IP:{}",
                    requestURI, className, methodName, formatArgs(args), elapsed, e.getMessage(), ip);
            throw e;
        }

        long elapsed = System.currentTimeMillis() - start;
        if (elapsed > 500) {
            log.warn("[{}] {}.{}({}) - {}ms [慢请求] - IP:{}",
                    requestURI, className, methodName, formatArgs(args), elapsed, ip);
        } else {
            log.info("[{}] {}.{}({}) - {}ms - IP:{}",
                    requestURI, className, methodName, formatArgs(args), elapsed, ip);
        }

        return result;
    }

    private String formatArgs(Object[] args) {
        if (args == null || args.length == 0) return "";
        return Arrays.toString(args).replaceAll("[\\n\\r]", "");
    }
}
