package com.ecommerce.order.service;

import java.util.List;

import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.entity.OrderStatus;

public interface OrderService {

    OrderResponse createOrder(Long userId, String authorizationHeader);

    OrderResponse getOrder(
            Long userId,
            Long orderId,
            String authorizationHeader);

    List<OrderResponse> getUserOrders(
            Long userId,
            String authorizationHeader);

    void cancelOrder(Long userId, Long orderId);
    OrderResponse updateOrderStatus(
        Long userId,
        Long orderId,
        OrderStatus status);
}