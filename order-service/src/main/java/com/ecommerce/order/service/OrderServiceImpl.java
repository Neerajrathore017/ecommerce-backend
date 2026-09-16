package com.ecommerce.order.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.order.client.CartClient;
import com.ecommerce.order.client.ProductClient;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.repository.OrderItemRepository;
import com.ecommerce.order.repository.OrderRepository;

import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.exception.ResourceNotFoundException;
import com.ecommerce.order.exception.UnauthorizedException;

import java.math.BigDecimal;

import com.ecommerce.order.dto.OrderItemResponse;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.entity.OrderStatus;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

        private final OrderRepository orderRepository;
        private final OrderItemRepository orderItemRepository;
        private final CartClient cartClient;
        private final ProductClient productClient;

        public OrderServiceImpl(
                        OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        CartClient cartClient,
                        ProductClient productClient) {

                this.orderRepository = orderRepository;
                this.orderItemRepository = orderItemRepository;
                this.cartClient = cartClient;
                this.productClient = productClient;
        }

        @Override
        public OrderResponse createOrder(
                        Long userId,
                        String authorizationHeader) {

                // 1. Get user's cart
                var cart = cartClient.getCart(authorizationHeader);

                // 2. Validate cart
                if (cart == null || cart.getItems() == null
                                || cart.getItems().isEmpty()) {

                        throw new IllegalArgumentException(
                                        "Cannot create order from an empty cart");
                }

                // 3. Create Order
                Order order = new Order();
                order.setUserId(userId);
                order.setStatus(OrderStatus.PENDING);

                // 4. Calculate total
                BigDecimal totalAmount = BigDecimal.ZERO;

                for (var cartItem : cart.getItems()) {

                        // Get latest product details
                        var product = productClient.getProduct(
                                        cartItem.getProductId(),
                                        authorizationHeader);

                        if (product == null || product.getPrice() == null) {
                                throw new IllegalArgumentException(
                                                "Product not available: "
                                                                + cartItem.getProductId());
                        }

                        BigDecimal itemTotal = product.getPrice()
                                        .multiply(
                                                        BigDecimal.valueOf(
                                                                        cartItem.getQuantity()));

                        totalAmount = totalAmount.add(itemTotal);

                        // Create OrderItem
                        OrderItem orderItem = new OrderItem();

                        orderItem.setOrder(order);
                        orderItem.setProductId(product.getId());
                        orderItem.setProductName(product.getName());
                        orderItem.setPrice(product.getPrice());
                        orderItem.setQuantity(cartItem.getQuantity());

                        order.addItem(orderItem);
                }

                // 5. Set total
                order.setTotalAmount(totalAmount);

                // 6. Save order
                Order savedOrder = orderRepository.save(order);
                cartClient.clearCart(authorizationHeader);

                // 7. Return response
                return buildOrderResponse(savedOrder);
        }

        @Override
        @Transactional(readOnly = true)
        public OrderResponse getOrder(
                        Long userId,
                        Long orderId,
                        String authorizationHeader) {

                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Order not found"));

                // Security check:
                // User can access only their own order
                if (!order.getUserId().equals(userId)) {
                        throw new UnauthorizedException(
                                        "Order does not belong to user");
                }

                return buildOrderResponse(order);
        }

        @Override
        @Transactional(readOnly = true)
        public List<OrderResponse> getUserOrders(
                        Long userId,
                        String authorizationHeader) {

                List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);

                return orders.stream()
                                .map(this::buildOrderResponse)
                                .toList();
        }

        @Override
        @Transactional
        public void cancelOrder(Long userId, Long orderId) {

                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

                if (!order.getUserId().equals(userId)) {
                        throw new UnauthorizedException(
                                        "You are not authorized to cancel this order");
                }

                if (order.getStatus() == OrderStatus.CANCELLED) {
                        throw new IllegalStateException(
                                        "Order is already cancelled");
                }

                if (order.getStatus() == OrderStatus.CONFIRMED) {
                        throw new IllegalStateException(
                                        "Confirmed order cannot be cancelled");
                }

                order.setStatus(OrderStatus.CANCELLED);

                orderRepository.save(order);
        }

        @Override
        @Transactional
        public OrderResponse updateOrderStatus(
                        Long userId,
                        Long orderId,
                        OrderStatus status) {

                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

                if (!order.getUserId().equals(userId)) {
                        throw new UnauthorizedException(
                                        "Order does not belong to user");
                }

                if (order.getStatus() == OrderStatus.CANCELLED) {
                        throw new IllegalStateException(
                                        "Cancelled order status cannot be changed");
                }

                if (status == OrderStatus.CANCELLED) {
                        throw new IllegalArgumentException(
                                        "Use cancel order API to cancel the order");
                }

                // Validate status transition
                if (order.getStatus() == OrderStatus.CONFIRMED
                                && status == OrderStatus.PENDING) {

                        throw new IllegalStateException(
                                        "Confirmed order cannot be changed back to pending");
                }

                order.setStatus(status);

                Order savedOrder = orderRepository.save(order);

                return buildOrderResponse(savedOrder);
        }

        private OrderResponse buildOrderResponse(Order order) {

                List<OrderItemResponse> items = order.getItems()
                                .stream()
                                .map(item -> new OrderItemResponse(
                                                item.getProductId(),
                                                item.getProductName(),
                                                item.getPrice(),
                                                item.getQuantity()))
                                .toList();

                return new OrderResponse(
                                order.getId(),
                                order.getTotalAmount(),
                                order.getStatus(),
                                order.getCreatedAt(),
                                items);
        }
}