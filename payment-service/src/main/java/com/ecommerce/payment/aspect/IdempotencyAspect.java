package com.ecommerce.payment.aspect;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect 
@Component 
public class IdempotencyAspect {

    @Around ("@annotation(com.ecommerce.payment.annotation.Idempotent)")
    public Object handleIdempotency(
            ProceedingJoinPoint joinPoint) throws Throwable {

        System.out.println("Before payment method");

        Object result = joinPoint.proceed();

        System.out.println("After payment method");

        return result;
    }
}