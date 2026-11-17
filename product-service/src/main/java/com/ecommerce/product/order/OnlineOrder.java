package com.ecommerce.product.order;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;


@Component 
// @Profile("prod")
// @ConditionalOnProperty ( prefix  = "onlinetype", value = "enabled",havingValue="true")
public class OnlineOrder implements Order {
    // @Value ("${userna}")
    // String username;

    @Override
    public void placeORder() {
        // System.out.println("Online Order  prod env  placed " + username);
    }
     @PostConstruct 
    void Test(){
        System.out.println("online  tst");
    }

    
}
