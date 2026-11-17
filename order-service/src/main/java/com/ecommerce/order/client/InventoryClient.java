package com.ecommerce.order.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.ecommerce.order.dto.InventoryResponse;
@Component 
public class InventoryClient {
    private final RestClient restClient;

    public InventoryClient() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8086")
                .build();
    }

    public InventoryResponse reserveStock(
            Long orderId,
            Long productId,
            Integer quantity,
            String authorizationHeader) {
        return restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/inventory/reserve")
                        .queryParam("orderId", orderId)
                        .queryParam("productId", productId)
                        .queryParam("quantity", quantity)
                        .build())
                .header("Authorization", authorizationHeader)
                .retrieve()
                .body(InventoryResponse.class);

    }

    public void confirmReservation(
            Long orderId,
            String authorizationHeader) {

        restClient.post().uri(uriBuilder -> uriBuilder
                .path("/inventory/confirm")
                .queryParam("orderId", orderId)
                .build())
                .header("Authorization", authorizationHeader)
                .retrieve()
                .toBodilessEntity();
    }

    public void releaseReservation(
            Long orderId,
            String authorizationHeader) {

        restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/inventory/release")
                        .queryParam("orderId", orderId)
                        .build())
                .header("Authorization", authorizationHeader)
                .retrieve()
                .toBodilessEntity();
    }

}
