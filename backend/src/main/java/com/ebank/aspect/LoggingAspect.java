package com.ebank.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("within(com.ebank.service..*)")
    public void serviceLayer() {
    }

    @Pointcut("within(com.ebank.controller..*)")
    public void controllerLayer() {
    }

    @Before("serviceLayer()")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Entering: {} with arguments: {}",
                joinPoint.getSignature().toShortString(),
                Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "serviceLayer()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("Exiting: {} with result: {}",
                joinPoint.getSignature().toShortString(),
                result);
    }

    @AfterThrowing(pointcut = "serviceLayer()", throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        log.error("Exception in: {} with message: {}",
                joinPoint.getSignature().toShortString(),
                error.getMessage());
    }

    @Around("controllerLayer()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - start;
            log.info("API {} completed in {} ms",
                    joinPoint.getSignature().toShortString(),
                    duration);
            return result;
        } catch (Throwable e) {
            long duration = System.currentTimeMillis() - start;
            log.error("API {} failed after {} ms with error: {}",
                    joinPoint.getSignature().toShortString(),
                    duration,
                    e.getMessage());
            throw e;
        }
    }
}
