package com.inventory_service.dto;

import lombok.Data;

/**
 * StockItem
 */
@Data 
public class StockItem {
    private Long productId;
    private Integer quantity;

}
