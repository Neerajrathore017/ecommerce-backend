package com.ecommerce.cart_service.dto;

import java.math.BigDecimal;

public record CartItemResponse (

    Long id,
        Long productId,
        String name,
        String description,
        BigDecimal price,
        Integer availableQuantity,
        String category,
        String imageUrl,
        String status,
        Integer quantity
){}