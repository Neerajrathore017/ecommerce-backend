package com.ecommerce.cart_service.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.cart_service.dto.CartItemResponse;
import com.ecommerce.cart_service.dto.CartResponse;
import com.ecommerce.cart_service.entity.Cart;
import com.ecommerce.cart_service.entity.CartItem;
import com.ecommerce.cart_service.repository.CartItemRepository;
import com.ecommerce.cart_service.repository.CartRepository;

import com.ecommerce.cart_service.exception.ProductNotFoundException;

@Service
@Transactional
public class CartServiceImpl implements CartService {
        private final ProductClient productClient;
        private final CartRepository cartRepository;
        private final CartItemRepository cartItemRepository;

        public CartServiceImpl(
                        ProductClient productClient,
                        CartRepository cartRepository,
                        CartItemRepository cartItemRepository) {
                this.productClient = productClient;
                this.cartRepository = cartRepository;
                this.cartItemRepository = cartItemRepository;
        }

        @Override
        @Transactional(readOnly = true)
        public CartResponse getCart(Long userId) {

                Cart cart = cartRepository.findByUserId(userId)
                                .orElse(null);

                if (cart == null) {
                        return new CartResponse(
                                        null,
                                        userId,
                                        List.of());
                }

                return buildCartResponse(cart);
        }

        @Override
        public CartResponse addItem(
                        Long userId,
                        Long productId,
                        Integer quantity) {
                if (!productClient.productExists(productId)) {
                        throw new ProductNotFoundException(
                                        "Product not found: " + productId);
                }
                if (quantity == null || quantity <= 0) {
                        throw new IllegalArgumentException(
                                        "Quantity must be greater than zero");
                }
                Cart cart = cartRepository.findByUserId(userId)
                                .orElseGet(() -> {

                                        Cart newCart = new Cart();
                                        newCart.setUserId(userId);

                                        return cartRepository.save(newCart);
                                });

                CartItem item = cartItemRepository
                                .findByCartIdAndProductId(
                                                cart.getId(),
                                                productId)
                                .orElse(null);

                if (item != null) {

                        item.setQuantity(
                                        item.getQuantity() + quantity);

                        cartItemRepository.save(item);

                } else {

                        CartItem newItem = new CartItem();

                        newItem.setCart(cart);
                        newItem.setProductId(productId);
                        newItem.setQuantity(quantity);

                        cartItemRepository.save(newItem);
                }

                return buildCartResponse(cart);
        }

        @Override
        public CartResponse updateItem(
                        Long userId,
                        Long itemId,
                        Integer quantity) {
                if (quantity == null || quantity <= 0) {
                        throw new IllegalArgumentException(
                                        "Quantity must be greater than zero");
                }
                Cart cart = cartRepository.findByUserId(userId)
                                .orElseThrow(() -> new RuntimeException("Cart not found"));

                CartItem item = cartItemRepository
                                .findById(itemId)
                                .orElseThrow(() -> new RuntimeException("Cart item not found"));

                if (!item.getCart().getId()
                                .equals(cart.getId())) {

                        throw new RuntimeException(
                                        "Cart item does not belong to user");
                }

                item.setQuantity(quantity);

                cartItemRepository.save(item);

                return buildCartResponse(cart);
        }

        @Override
        public void removeItem(
                        Long userId,
                        Long itemId) {

                Cart cart = cartRepository.findByUserId(userId)
                                .orElseThrow(() -> new RuntimeException("Cart not found"));

                CartItem item = cartItemRepository
                                .findById(itemId)
                                .orElseThrow(() -> new RuntimeException("Cart item not found"));

                if (!item.getCart().getId()
                                .equals(cart.getId())) {

                        throw new RuntimeException(
                                        "Cart item does not belong to user");
                }

                cartItemRepository.delete(item);
        }

        @Override
        public void clearCart(Long userId) {

                Cart cart = cartRepository.findByUserId(userId)
                                .orElseThrow(() -> new RuntimeException("Cart not found"));

                List<CartItem> items = cartItemRepository.findByCartId(cart.getId());

                cartItemRepository.deleteAll(items);
        }

        private CartResponse buildCartResponse(Cart cart) {

                List<CartItemResponse> items = cartItemRepository
                                .findByCartId(cart.getId())
                                .stream()
                                .map(item -> new CartItemResponse(
                                                item.getId(),
                                                item.getProductId(),
                                                item.getQuantity()))
                                .toList();

                return new CartResponse(
                                cart.getId(),
                                cart.getUserId(),
                                items);
        }
}