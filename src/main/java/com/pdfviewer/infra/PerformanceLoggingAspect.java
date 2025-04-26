package com.pdfviewer.infra;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Aspect
@Component
public class PerformanceLoggingAspect {

    @Around("@annotation(com.pdfviewer.infra.LogPerformance)")
    public Object loggingExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            Object result = joinPoint.proceed();
            stopWatch.stop();
            log.info("[{}] executed in {} seconds", joinPoint.getSignature().getName(), stopWatch.getTotalTimeSeconds());
            return result;
        } catch (Throwable throwable) {
            stopWatch.stop();
            log.error("Exception in [{}] after {} seconds", joinPoint.getSignature().getName(), stopWatch.getTotalTimeSeconds(), throwable);
            throw throwable;
        }
    }
}
