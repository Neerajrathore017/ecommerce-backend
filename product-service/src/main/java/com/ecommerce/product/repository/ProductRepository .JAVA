package com.ecommerce.product.repository;

public class ProductRepository {

    public interface ProductRepository extends JpaRepository<Product, Long> {
    }

}