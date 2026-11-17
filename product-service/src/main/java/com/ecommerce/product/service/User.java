package com.ecommerce.product.service;

import org.springframework.stereotype.Service;

import com.ecommerce.product.order.Order;

import jakarta.annotation.PostConstruct;

@Service 
public class User {

    private final Order order;

    public User(Order order){
        this.order=order;
    }
    @PostConstruct 
    void CurrentEnv(){
        System.out.println("Ordr caleed in service "+order);
    }

    public  void createOrder(){
        order.placeORder();
    }
    
}
