package com.inventory_service.controller;

import java.math.BigInteger;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inventory_service.dto.InventoryResponse;
import com.inventory_service.entity.Inventory;
import com.inventory_service.service.InventoryService;
import com.inventory_service.service.StockReserve;


@RestController
@RequestMapping("/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/addStock")
    public ResponseEntity<Inventory> addStock(@RequestParam Long productId, @RequestParam Integer quantity) {
        // inventoryService.addStock(productId, quantity);
        return ResponseEntity.ok(inventoryService.addStock(productId, quantity).orElse(null));
    }

    @PostMapping("/removeStock")
    public ResponseEntity<InventoryResponse> removeStock(@RequestParam Long productId,
            @RequestParam Integer quantity) {
        InventoryResponse inventoryResponse = inventoryService.removeStock(productId, quantity);
        return ResponseEntity.ok(inventoryResponse);
    }
    @GetMapping("/product")
    public String getProductStock(@RequestParam Integer productId) {
        return new String();
    }
    

    // @PostMapping("/reserve")
    // public ResponseEntity<InventoryResponse> reserveStock(@RequestParam Long productId,
    //         @RequestParam Integer quantity) {
    //     InventoryResponse inventoryResponse = StockReserve.reserveStock(productId, quantity);
    //     return ResponseEntity.ok(inventoryResponse);
    // }
}
