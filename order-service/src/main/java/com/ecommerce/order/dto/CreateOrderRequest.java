package com.ecommerce.order.dto;

public record CreateOrderRequest(
        String paymentMethod
) {}