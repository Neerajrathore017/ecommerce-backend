package com.ecommerce.order.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader("Authorization") String authorizationHeader) {

        Long userId = jwt.getClaim("userId");

        return ResponseEntity.ok(
                orderService.createOrder(
                        userId,
                        authorizationHeader));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader("Authorization") String authorizationHeader) {

        Long userId = jwt.getClaim("userId");

        return ResponseEntity.ok(
                orderService.getOrder(
                        userId,
                        orderId,
                        authorizationHeader));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getUserOrders(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader("Authorization") String authorizationHeader) {

        Long userId = jwt.getClaim("userId");

        return ResponseEntity.ok(
                orderService.getUserOrders(
                        userId,
                        authorizationHeader));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal Jwt jwt) {

        Long userId = jwt.getClaim("userId");

        orderService.cancelOrder(userId, orderId);

        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{orderId}/status")
public ResponseEntity<OrderResponse> updateOrderStatus(
        @PathVariable Long orderId,
        @RequestParam OrderStatus status,
        @AuthenticationPrincipal Jwt jwt) {

    Long userId = jwt.getClaim("userId");

    return ResponseEntity.ok(
            orderService.updateOrderStatus(
                    userId,
                    orderId,
                    status));
}
}