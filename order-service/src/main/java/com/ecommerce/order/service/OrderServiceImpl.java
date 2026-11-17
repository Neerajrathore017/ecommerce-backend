package com.ecommerce.order.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.order.client.CartClient;
import com.ecommerce.order.client.InventoryClient;
import com.ecommerce.order.client.PaymentClient;
import com.ecommerce.order.client.ProductClient;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.dto.PaymentResponse;
import com.ecommerce.order.repository.OrderItemRepository;
import com.ecommerce.order.repository.OrderRepository;

import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.exception.ResourceNotFoundException;
import com.ecommerce.order.exception.UnauthorizedException;
import com.ecommerce.order.dto.InventoryResponse;
import com.ecommerce.order.dto.OrderItemResponse;

@Service
public class OrderServiceImpl implements OrderService {

        private final OrderRepository orderRepository;
        private final OrderItemRepository orderItemRepository;
        private final CartClient cartClient;
        private final ProductClient productClient;
        private final InventoryClient inventoryClient;
        private final PaymentClient paymentClient;

        public OrderServiceImpl(
                        OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        CartClient cartClient,
                        ProductClient productClient) {

                this.orderRepository = orderRepository;
                this.orderItemRepository = orderItemRepository;
                this.cartClient = cartClient;
                this.productClient = productClient;
                this.inventoryClient = new InventoryClient();
                this.paymentClient = new PaymentClient();
        }

        @Override
        public OrderResponse createOrder(
                        Long userId,
                        String paymentMethod,
                        String authorizationHeader) {

                // 1. Get user's cart
                var cart = cartClient.getCart(authorizationHeader);

                if (cart == null || cart.getItems() == null
                                || cart.getItems().isEmpty()) {
                        throw new IllegalArgumentException(
                                        "Cannot create order from an empty cart");
                }

                // 2. Create order
                Order order = new Order();
                order.setUserId(userId);
                order.setStatus(OrderStatus.PENDING);

                BigDecimal totalAmount = BigDecimal.ZERO;

                // 3. Get product details and prepare order items
                for (var cartItem : cart.getItems()) {

                        var product = productClient.getProduct(
                                        cartItem.getProductId(),
                                        authorizationHeader);

                        if (product == null || product.getPrice() == null) {
                                throw new IllegalArgumentException(
                                                "Product not available: " + cartItem.getProductId());
                        }

                        if (cartItem.getQuantity() == null || cartItem.getQuantity() <= 0) {
                                throw new IllegalArgumentException("Invalid product quantity");
                        }

                        BigDecimal itemTotal = product.getPrice()
                                        .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

                        totalAmount = totalAmount.add(itemTotal);

                        OrderItem orderItem = new OrderItem();
                        orderItem.setOrder(order);
                        orderItem.setProductId(product.getId());
                        orderItem.setProductName(product.getName());
                        orderItem.setPrice(product.getPrice());
                        orderItem.setQuantity(cartItem.getQuantity());

                        order.addItem(orderItem);
                }

                // 4. Set total and save PENDING order
                order.setTotalAmount(totalAmount);

                Order savedOrder = orderRepository.save(order);

                try {

                        // 5. Reserve inventory for every item
                        for (OrderItem item : savedOrder.getItems()) {

                                InventoryResponse response = inventoryClient.reserveStock(
                                                savedOrder.getId(),
                                                item.getProductId(),
                                                item.getQuantity(),
                                                authorizationHeader);

                                if (response == null
                                                || !"RESERVED".equalsIgnoreCase(response.status())) {
                                        throw new IllegalStateException(
                                                        "Inventory reservation failed for product: "
                                                                        + item.getProductId());
                                }
                        }

                } catch (Exception ex) {

                        // 6. Release any stock already reserved for this order
                        try {
                                inventoryClient.releaseReservation(
                                                savedOrder.getId(),
                                                authorizationHeader);
                        } catch (Exception releaseException) {
                                ex.addSuppressed(releaseException);
                        }

                        // 7. Mark order as FAILED
                        savedOrder.setStatus(OrderStatus.FAILED);
                        orderRepository.save(savedOrder);

                        throw new IllegalStateException(
                                        "Order failed because inventory reservation was unsuccessful",
                                        ex);
                }

                // 8. Return order (payment integration comes next)
                // 8. Initiate payment
                PaymentResponse paymentResponse;

                try {
                        paymentResponse = paymentClient.initiatePayment(
                                        savedOrder.getId(),
                                        savedOrder.getTotalAmount(),
                                        paymentMethod,
                                        authorizationHeader);

                        if (paymentResponse == null ||
                                        !"SUCCESS".equalsIgnoreCase(paymentResponse.status())) {
                                throw new IllegalStateException("Payment failed");
                        }

                } catch (Exception ex) {

                        // Payment failed: release reserved inventory
                        try {
                                inventoryClient.releaseReservation(
                                                savedOrder.getId(),
                                                authorizationHeader);
                        } catch (Exception releaseException) {
                                ex.addSuppressed(releaseException);
                        }

                        savedOrder.setStatus(OrderStatus.FAILED);
                        orderRepository.save(savedOrder);

                        throw new IllegalStateException(
                                        "Payment failed. Inventory reservation released.",
                                        ex);
                }
                // 9. Payment successful: confirm inventory
                try {
                        inventoryClient.confirmReservation(
                                        savedOrder.getId(),
                                        authorizationHeader);

                } catch (Exception ex) {

                        // Payment has succeeded, so do NOT release stock blindly.
                        // A refund/void compensation must be implemented for this case.
                        throw new IllegalStateException(
                                        "Payment succeeded but inventory confirmation failed. "
                                                        + "Manual compensation is required.",
                                        ex);
                }
                // 10. Update order status
                savedOrder.setStatus(OrderStatus.CONFIRMED);

                Order confirmedOrder = orderRepository.save(savedOrder);

                // 11. Return final order
                return buildOrderResponse(confirmedOrder);
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