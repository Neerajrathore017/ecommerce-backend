package com.ecommerce.product.serviceImpl;

import org.springframework.stereotype.Component;

import com.ecommerce.product.service.TestInterface;

@Component 
public class ImplTest2 implements  TestInterface{

    @Override
    public void pay() {
        System.out.println("calll from Test2");
    }
    
    
}
