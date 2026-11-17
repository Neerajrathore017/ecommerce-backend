package com.inventory_service.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventory_service.entity.ReservationStatus;
import com.inventory_service.entity.StockReservation;

public interface StockReservationRepository extends JpaRepository<StockReservation, Long> {

    List<StockReservation> findByOrderId(Long orderId);

    Optional<StockReservation> findByOrderIdAndProductId(
            Long orderId,
            Long productId
    );

    List<StockReservation> findByOrderIdAndStatus(
            Long orderId,
            ReservationStatus status
    );
//    Optional<List<StockReservation>> findByOrderId(Long orderId);

//     Optional<List<StockReservation>> findByOrderIdAndStatus(
//         Long orderId,
//         ReservationStatus status
//     );
}
