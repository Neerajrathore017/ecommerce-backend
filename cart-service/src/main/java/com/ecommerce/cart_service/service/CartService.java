package com.ecommerce.cart_service.service;

import com.ecommerce.cart_service.dto.CartResponse;

public interface CartService {

    CartResponse getCart(Long userId);

    CartResponse addItem(
            Long userId,
            Long productId,
            Integer quantity
    );

    CartResponse updateItem(
            Long userId,
            Long itemId,
            Integer quantity
    );

    void removeItem(
            Long userId,
            Long itemId
    );

    void clearCart(Long userId);
}