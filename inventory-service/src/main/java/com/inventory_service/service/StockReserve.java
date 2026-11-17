
package com.inventory_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventory_service.dto.InventoryResponse;
import com.inventory_service.entity.Inventory;
import com.inventory_service.entity.ReservationStatus;
import com.inventory_service.entity.StockReservation;
import com.inventory_service.repo.InventoryRepository;
import com.inventory_service.repo.StockReservationRepository;

@Service
public class StockReserve {

    private final InventoryRepository inventoryRepository;
    private final StockReservationRepository reservationRepository;

    public StockReserve(
            InventoryRepository inventoryRepository,
            StockReservationRepository reservationRepository) {
        this.inventoryRepository = inventoryRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public InventoryResponse reserveStock(
            Long orderId,
            Long productId,
            Integer quantity) {

        // 1. Validate request
        if (orderId == null || productId == null ||
                quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Invalid reservation request");
        }

        // 2. Check duplicate reservation
        var existing = reservationRepository
                .findByOrderIdAndProductId(orderId, productId);

        if (existing.isPresent()) {
            StockReservation reservation = existing.get();

            if (reservation.getStatus() == ReservationStatus.RESERVED) {
                Inventory inventory = inventoryRepository
                        .findByProductId(productId)
                        .orElseThrow(() -> new RuntimeException("Inventory not found"));

                return new InventoryResponse(
                        productId,
                        inventory.getQuantity(),
                        reservation.getQuantity(),
                        "RESERVED"
                );
            }

            throw new IllegalStateException(
                    "Reservation already exists with status: "
                            + reservation.getStatus()
            );
        }

        // 3. Lock inventory row
        Inventory inventory = inventoryRepository
                .findByProductIdForUpdate(productId)
                .orElseThrow(() -> new RuntimeException(
                        "Inventory for product " + productId + " not found"));

        // 4. Check available stock
        if (inventory.getQuantity() < quantity) {
            return new InventoryResponse(
                    productId,
                    inventory.getQuantity(),
                    quantity,
                    "OUT_OF_STOCK"
            );
        }

        // 5. Move stock from available to reserved
        inventory.setQuantity(inventory.getQuantity() - quantity);

        int reserved = inventory.getReservedQuantity() == null
                ? 0 : inventory.getReservedQuantity();

        inventory.setReservedQuantity(reserved + quantity);

        inventoryRepository.save(inventory);

        // 6. Create reservation record
        StockReservation reservation = new StockReservation();
        reservation.setOrderId(orderId);
        reservation.setProductId(productId);
        reservation.setQuantity(quantity);
        reservation.setStatus(ReservationStatus.RESERVED);

        reservationRepository.save(reservation);

        return new InventoryResponse(
                productId,
                inventory.getQuantity(),
                quantity,
                "RESERVED"
        );
    }

    @Transactional
public void confirmReservation(Long orderId) {

    var reservations = reservationRepository
            .findByOrderIdAndStatus(
                    orderId,
                    ReservationStatus.RESERVED
            );

    for (StockReservation reservation : reservations) {

        Inventory inventory = inventoryRepository
                .findByProductIdForUpdate(reservation.getProductId())
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        inventory.setReservedQuantity(
                inventory.getReservedQuantity() - reservation.getQuantity()
        );

        reservation.setStatus(ReservationStatus.CONFIRMED);

        inventoryRepository.save(inventory);
        reservationRepository.save(reservation);
    }
}
@Transactional
public void releaseReservation(Long orderId) {

    var reservations = reservationRepository
            .findByOrderIdAndStatus(
                    orderId,
                    ReservationStatus.RESERVED
            );

    for (StockReservation reservation : reservations) {

        Inventory inventory = inventoryRepository
                .findByProductIdForUpdate(reservation.getProductId())
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        inventory.setQuantity(
                inventory.getQuantity() + reservation.getQuantity()
        );

        inventory.setReservedQuantity(
                inventory.getReservedQuantity() - reservation.getQuantity()
        );

        reservation.setStatus(ReservationStatus.RELEASED);

        inventoryRepository.save(inventory);
        reservationRepository.save(reservation);
    }
}
}

// package com.inventory_service.service;

// import org.springframework.stereotype.Service;

// import com.inventory_service.dto.InventoryResponse;
// import com.inventory_service.entity.Inventory;
// import com.inventory_service.entity.ReservationStatus;
// import com.inventory_service.entity.StockReservation;
// import com.inventory_service.repo.InventoryRepository;
// import com.inventory_service.repo.StockReservationRepository;

// import jakarta.transaction.Transactional;

// @Service
// public class StockReserve {
//     private final InventoryRepository inventoryRepository;
//     private final StockReservationRepository reservationRepository;

//     public StockReserve(InventoryRepository inventoryRepository, StockReservationRepository reservationRepository) {
//         this.inventoryRepository = inventoryRepository;
//         this.reservationRepository = reservationRepository;
//     }
//     @Transactional 
//     public InventoryResponse reserveStock(Long productId, Integer quantity) {
//         Inventory inventory = inventoryRepository.findByProductId(productId)
//                 .orElseThrow(() -> new RuntimeException("Inventory (Product " + productId + ") not found"));
//         if (inventory.getQuantity() >= quantity.intValue()) {
//             inventory.setQuantity(inventory.getQuantity() - quantity.intValue());
//             inventoryRepository.save(inventory);

//             StockReservation stockReservation = new StockReservation();
//             stockReservation.setQuantity(quantity);
//             stockReservation.setOrderId(productId);// Replace it with order id \
//             stockReservation.setStatus(ReservationStatus.RESERVED);
//             stockReservation.setProductId(productId);
//             reservationRepository.save(stockReservation);

//             InventoryResponse inventoryResponse = new InventoryResponse(productId, inventory.getQuantity(), quantity,
//                     "RESERVED");
//             return inventoryResponse;
//         }
//         // Add if stock under reserve on that time stock should be out of stock
//         InventoryResponse inventoryResponse = new InventoryResponse(productId, inventory.getQuantity(), quantity,
//                 "OUT_OF_STOCK");
//         return inventoryResponse;
//     }

// }
