package com.ecommerce.cart_service.dto;

import java.math.BigDecimal;

// import com.ecommerce.product.entity.ProductStatus;
public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer quantity,
        String category,
        String imageUrl,
        String status
) {}