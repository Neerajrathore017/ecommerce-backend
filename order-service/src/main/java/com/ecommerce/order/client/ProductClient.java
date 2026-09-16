package com.ecommerce.order.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductClient {

    private final RestClient restClient;

    public ProductClient() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8081")
                .build();
    }

    public ProductResponse getProduct(
            Long productId,
            String authorizationHeader) {

        return restClient.get()
                .uri("/products/{id}", productId)
                .header("Authorization", authorizationHeader)
                .retrieve()
                .body(ProductResponse.class);
    }
}