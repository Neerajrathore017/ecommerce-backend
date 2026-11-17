package com.ecommerce.cart_service.service;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.ecommerce.cart_service.dto.ProductResponse;

@Component
public class ProductClient {

    private final RestClient restClient;

    public ProductClient() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8081")
                .build();
    }

    public boolean productExists(Long productId) {

        try {
            restClient.get()
                    .uri("/products/{id}", productId)
                    .retrieve()
                    .toBodilessEntity();

            return true;

        } catch (Exception e) {
            return false;
        }
    }
    public  ProductResponse getProduct(Long productId){
        return restClient.get()
        .uri("http://localhost:8081/products/{id}",productId)
        .retrieve()
                .body(ProductResponse.class);
    }
}