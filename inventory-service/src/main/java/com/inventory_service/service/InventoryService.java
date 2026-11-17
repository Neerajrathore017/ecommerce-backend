package com.inventory_service.service;

import java.math.BigInteger;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.inventory_service.dto.InventoryResponse;
import com.inventory_service.entity.Inventory;
import com.inventory_service.repo.InventoryRepository;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public Optional<Inventory> addStock(Long productId, Integer quantity) {
        Optional<Inventory> inventory = inventoryRepository.findByProductId(productId);
        if (inventory.isPresent()) {
            Inventory curr = inventory.get();
            curr.setQuantity(curr.getQuantity() + quantity.intValue());
            inventoryRepository.save(curr);
            return Optional.of(curr);
        } else {
            Inventory newInventory = new Inventory();
            newInventory.setProductId(productId);
            newInventory.setQuantity(quantity.intValue());
            inventoryRepository.save(newInventory);
            return Optional.of(newInventory);

        }
    }

    public InventoryResponse removeStock(Long productId, Integer quantity) {

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory (Product " + productId + ") not found"));

        InventoryResponse inventoryResponse = new InventoryResponse(productId, inventory.getQuantity(), quantity, "OUT_OF_STOCK");

        if (inventory.getQuantity() >= quantity.intValue()) {
            inventory.setQuantity(inventory.getQuantity() - quantity.intValue());
            inventoryRepository.save(inventory);
            inventoryResponse = new InventoryResponse(productId, inventory.getQuantity(), quantity, "IN_STOCK");
        } else {
            inventoryResponse = new InventoryResponse(productId, inventory.getQuantity(),quantity,
                    inventory.getQuantity() > 0 ? "Only " + inventory.getQuantity() + " left" : "OUT_OF_STOCK" );
        }
        return inventoryResponse;

        // Optional<Inventory> inventory =
        // inventoryRepository.findByProductId(productId);
        // if (inventory.isPresent()) {
        // Inventory curr = inventory.get();
        // if (curr.getQuantity() >= quantity.intValue()) {
        // curr.setQuantity(curr.getQuantity() - quantity.intValue());
        // inventoryRepository.save(curr);
        // return Optional.of(curr);
        // } else {
        // // Not enough stock available
        // return Optional.empty();
        // }
        // } else {
        // // Inventory not found for the given productId
        // return Optional.empty();
        // }
    }
    //--------------------Move to stock reserver service-------------------------
    // public InventoryResponse reserveStock(Long productId, Integer quantity) {
    //     Inventory inventory = inventoryRepository.findByProductId(productId)
    //             .orElseThrow(() -> new RuntimeException("Inventory (Product " + productId + ") not found"));
    //     if (inventory.getQuantity() >= quantity.intValue()) {
    //         inventory.setQuantity(inventory.getQuantity() - quantity.intValue());
    //         inventoryRepository.save(inventory);
    //         InventoryResponse inventoryResponse = new InventoryResponse(productId, inventory.getQuantity(), quantity,"RESERVED");
    //         return inventoryResponse;
    //     }
    //     // Add if stock under reserve on that time stock should be out of stock
    //     InventoryResponse inventoryResponse = new InventoryResponse(productId, inventory.getQuantity(), quantity,"OUT_OF_STOCK");
    //     return inventoryResponse;
    // }

}
