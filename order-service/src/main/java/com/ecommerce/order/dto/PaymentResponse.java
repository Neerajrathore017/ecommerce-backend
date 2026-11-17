package com.ecommerce.order.dto;

import java.math.BigDecimal;

public record PaymentResponse(
        Long orderId,
        BigDecimal amount,
        String paymentMethod,
        String status
) {}