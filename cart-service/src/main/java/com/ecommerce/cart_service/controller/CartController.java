package com.ecommerce.cart_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.cart_service.dto.CartResponse;
import com.ecommerce.cart_service.service.CartService;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(
            @AuthenticationPrincipal Jwt jwt) {

        Long userId = jwt.getClaim("userId");

        return ResponseEntity.ok(
                cartService.getCart(userId)
        );
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {

        Long userId = jwt.getClaim("userId");

        return ResponseEntity.ok(
                cartService.addItem(
                        userId,
                        productId,
                        quantity
                )
        );
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> updateItem(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long itemId,
            @RequestParam Integer quantity) {

        Long userId = jwt.getClaim("userId");

        return ResponseEntity.ok(
                cartService.updateItem(
                        userId,
                        itemId,
                        quantity
                )
        );
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> removeItem(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long itemId) {

        Long userId = jwt.getClaim("userId");

        cartService.removeItem(userId, itemId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(
            @AuthenticationPrincipal Jwt jwt) {

        Long userId = jwt.getClaim("userId");

        cartService.clearCart(userId);

        return ResponseEntity.noContent().build();
    }
}