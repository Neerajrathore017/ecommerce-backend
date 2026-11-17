package com.inventory_service.dto;

import java.util.List;

import lombok.Data;
@Data 
public class ReserveStockRequest {
    private Long productId;
    private List<StockItem> stockItems;
}
