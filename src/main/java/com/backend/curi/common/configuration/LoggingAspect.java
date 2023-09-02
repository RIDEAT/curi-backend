package com.backend.curi.common.configuration;

import com.backend.curi.CuriApplication;
import com.backend.curi.common.Common;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {
    private static Logger log = LoggerFactory.getLogger(LoggingAspect.class);
    private final Common common;

    @Before("execution(* com.backend.curi.*.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {


        String packageName = joinPoint.getTarget().getClass().getPackage().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String userId = common.getCurrentUser().getUserId();
        log.info("userId : {} Before {}.{}.{})",
                userId, packageName, className, methodName);
    }

    @After("execution(* com.backend.curi.*.service.*.*(..))")
    public void logAfter(JoinPoint joinPoint) {
        String packageName = joinPoint.getTarget().getClass().getPackage().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String userId = common.getCurrentUser().getUserId();
        log.info("userId : {} After {}.{}.{})",
                userId, packageName, className, methodName);
    }
}
