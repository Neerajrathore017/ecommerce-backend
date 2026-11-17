package com.inventory_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(
    name = "inventory",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_inventory_product",
            columnNames = "product_id"
        )
    }
)
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    // Stock available for new orders
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 0;

    // Stock reserved by pending orders
    @Column(name = "reserved_quantity")
    private Integer reservedQuantity = 0;
}
// package com.inventory_service.entity;
// import jakarta.persistence.Entity;
// import jakarta.persistence.Table;
// import jakarta.persistence.Id;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Column;

// import lombok.Data;

// @Entity
// @Data
// @Table(name = "inventory")
// public class Inventory {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     @Column(name = "product_id")
//     private Long productId;

//     @Column(name = "quantity")
//     private Integer quantity;

//     // Constructors, getters, and setters

// }