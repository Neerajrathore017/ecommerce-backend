package com.ecommerce.product.serviceImpl;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.ecommerce.product.service.TestInterface;

import jakarta.annotation.PostConstruct;

@Primary
@Component 
public class ImplTest1 implements  TestInterface{
    @PostConstruct 
    public void init(){
        System.out.println(" does itget called ?????");
    }

    @Override
    public void pay() {
       System.out.println("Call from Test 1");
    }
    
}
