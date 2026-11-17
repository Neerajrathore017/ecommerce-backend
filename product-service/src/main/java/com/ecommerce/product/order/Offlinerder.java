package com.ecommerce.product.order;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component 
// @Profile("dev")
// @ConditionalOnProperty ( prefix  = "offlinetype", value = "enabled",havingValue="true")
public class Offlinerder{

    // @Value("${userna}")
    // String username;
    @PostConstruct
    void Test(){
        // System.out.println("Offline Tst  in Dev Env "+ username);
    }

    // @Override
    // public void placeORder() {
    //     System.out.println("Offline Order placed");
    // }
    
}
