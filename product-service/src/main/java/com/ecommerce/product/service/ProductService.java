package com.ecommerce.product.service;

import java.util.List;

import com.ecommerce.product.entity.Product;

public interface ProductService {

    Product createProduct(Product product);
    List<Product> getAllProducts();
    Product getProductById(Long id);
    Product updateProduct(Long id, Product product);
    void deleteProduct(Long id);
}
