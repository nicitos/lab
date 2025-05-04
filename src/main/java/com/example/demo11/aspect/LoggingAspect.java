package com.example.demo11.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.example.demo11.controller.*.*(..))")
    public void logBeforeController(JoinPoint joinPoint) {
        logger.info("Вход в метод: {} с аргументами: {}", joinPoint.getSignature().toShortString(), joinPoint.getArgs());
    }

    @AfterThrowing(pointcut = "execution(* com.example.demo11.controller.*.*(..))", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        logger.error("Ошибка в методе: {} с причиной: {}", joinPoint.getSignature().toShortString(), ex.getMessage());
    }
}