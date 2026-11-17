package com.ecommerce.order.dto;

public record InventoryResponse(
        Long productId,
        Integer availablequantity,
        Integer quantity,
        String status
) {}