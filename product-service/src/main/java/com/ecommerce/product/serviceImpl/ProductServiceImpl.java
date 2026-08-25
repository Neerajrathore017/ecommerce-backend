package com.ecommerce.product.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {
    // Implement the methods defined in the ProductService interface
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setCategory(request.getCategory());
        product.setImageUrl(request.getImageUrl());

        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException(
                "Product not found with id: " + id));
        return mapToResponse(product);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product existingProduct = productRepository.findById(id).orElse(null);
        if (existingProduct != null) {

            existingProduct.setName(request.getName());
            existingProduct.setDescription(request.getDescription());
            existingProduct.setPrice(request.getPrice());
            existingProduct.setQuantity(request.getQuantity());
            existingProduct.setCategory(request.getCategory());
            existingProduct.setImageUrl(request.getImageUrl());

            productRepository.save(existingProduct);
            return mapToResponse(existingProduct);
        }
        return null;
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Product not found with id: " + id));

        productRepository.delete(product);
    }

    private ProductResponse mapToResponse(Product product) {

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getCategory(),
                product.getImageUrl());
    }
}