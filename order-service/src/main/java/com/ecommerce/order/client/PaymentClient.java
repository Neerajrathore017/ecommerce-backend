package com.ecommerce.order.client;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.ecommerce.order.dto.PaymentRequest;
import com.ecommerce.order.dto.PaymentResponse;

@Component
public class PaymentClient {

    private final RestClient restClient;

    public PaymentClient() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8085")
                .build();
    }

    public PaymentResponse initiatePayment(
            Long orderId,
            BigDecimal amount,
            String paymentMethod,
            String authorizationHeader) {

        PaymentRequest request =
                new PaymentRequest(orderId, amount, paymentMethod);

        return restClient.post()
                .uri("/payments")
                .header("Authorization", authorizationHeader)
                .body(request)
                .retrieve()
                .body(PaymentResponse.class);
    }
}

