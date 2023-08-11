package com.backend.curi.common.configuration;

import com.backend.curi.CuriApplication;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.backend.curi.*.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {


        String packageName = joinPoint.getTarget().getClass().getPackage().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        log.info("Before executing: {}.{}.{}", packageName, className, methodName);
    }
}
