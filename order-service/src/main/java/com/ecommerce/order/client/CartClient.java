package com.ecommerce.order.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CartClient {

    private final RestClient restClient;

    public CartClient() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8083")
                .build();
    }

    public CartResponse getCart(String authorizationHeader) {

        return restClient.get()
                .uri("/cart")
                .header("Authorization", authorizationHeader)
                .retrieve()
                .body(CartResponse.class);
    }
    public void clearCart(String authorizationHeader) {

    restClient.delete()
            .uri("/cart")
            .header("Authorization", authorizationHeader)
            .retrieve()
            .toBodilessEntity();
}
}