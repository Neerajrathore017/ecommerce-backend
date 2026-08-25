package com.ecommerce.product.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private BigDecimal price;

    private String imageUrl;
    
    @Column(nullable = false)
    private Integer quantity;
    
    private String category;
     public Product() {
    }
     public Long getId() {
         return id;
     }
     public void setId(Long id) {
         this.id = id;
     }
     public String getName() {
         return name;
     }
     public void setName(String name) {
         this.name = name;
     }
     public String getDescription() {
         return description;
     }
     public void setDescription(String description) {
         this.description = description;
     }
     public BigDecimal getPrice() {
         return price;
     }
     public void setPrice(BigDecimal price) {
         this.price = price;
     }
     public String getImageUrl() {
         return imageUrl;
     }
     public void setImageUrl(String imageUrl) {
         this.imageUrl = imageUrl;
     }
     public Integer getQuantity() {
         return quantity;
     }
     public void setQuantity(Integer quantity) {
         this.quantity = quantity;
     }
     public String getCategory() {
         return category;
     }
     public void setCategory(String category) {
         this.category = category;
     }
     public Product(Long id, String name, String description, BigDecimal price, String imageUrl, Integer quantity,
            String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.category = category;
     }


}                                                                                           