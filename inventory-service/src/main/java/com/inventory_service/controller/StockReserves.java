package com.inventory_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inventory_service.dto.InventoryResponse;

@RestController
@RequestMapping(path = "/inventory")
public class StockReserves {
    private final com.inventory_service.service.StockReserve stockReserve;

    public StockReserves(com.inventory_service.service.StockReserve stockReserve) {
        this.stockReserve = stockReserve;
    }

    @PostMapping("/reserve")
    public ResponseEntity<InventoryResponse> reserveStock(
            @RequestParam Long orderId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {

        InventoryResponse response = stockReserve.reserveStock(
                orderId,
                productId,
                quantity);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirmReservation(
            @RequestParam Long orderId) {

        stockReserve.confirmReservation(orderId);

        return ResponseEntity.ok("Reservation confirmed successfully");
    }
    @PostMapping("/release")
public ResponseEntity<String> releaseReservation(
        @RequestParam Long orderId) {

    stockReserve.releaseReservation(orderId);

    return ResponseEntity.ok("Reservation released successfully");
}
}
