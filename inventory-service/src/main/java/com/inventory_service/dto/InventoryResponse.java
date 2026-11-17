package com.inventory_service.dto;


public record InventoryResponse(Long productId, Integer availablequantity, Integer quantity, String status) {

    
}
