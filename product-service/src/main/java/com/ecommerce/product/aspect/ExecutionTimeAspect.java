package com.ecommerce.product.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.ecommerce.product.annotation.LogExecutionTime;

@Aspect 
@Component 
public class ExecutionTimeAspect {

    @Around ("@annotation(com.ecommerce.product.annotation.LogExecutionTime)")//point cut
    public Object logTime(ProceedingJoinPoint joinPoint) throws Throwable{
        long start=System.nanoTime();
         System.out.println(
        "ASPECT ENTER: " + joinPoint.getSignature().getName()
        + " | " + java.util.UUID.randomUUID()
        + " | " + java.time.LocalTime.now()
    );



        Method method=((MethodSignature) joinPoint.getSignature()).getMethod();
        if(method.isAnnotationPresent(LogExecutionTime.class)){
            LogExecutionTime logExecutionTime=method.getAnnotation(LogExecutionTime.class);
            System.out.println("Name of annotaion : "+ logExecutionTime.name());
        }
        try{
           return  joinPoint.proceed();
    
        } finally{
            long time=System.nanoTime()-start;
            System.out.println("Time Taken to fetch produts : "+ time);
            System.out.println(  "ASPECT EXIT: "
            +
                joinPoint.getSignature().getName()
                + " took " + time / 1_000_000 + " ms"
            );
            
        }
    }
    
}
