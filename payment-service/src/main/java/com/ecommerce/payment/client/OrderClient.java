package com.ecommerce.payment.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
@Component 
public class OrderClient {
        private final RestClient restClient;

        public OrderClient() {
            this.restClient = RestClient.builder()
                    .baseUrl("http://localhost:8084")
                    .build();
        }

        public void updateOrderStatus(Long orderId, String status, String authorization) {
            
            restClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path("/orders/{orderId}/status")
                        .queryParam("status", status)
                        .build(orderId))
                .header("Authorization", authorization)
                .retrieve()
                .toBodilessEntity();
        }

    
}
