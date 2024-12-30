package com.farmstock.repository;

import com.farmstock.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByStockId(Long stockId);
    List<StockMovement> findByUserId(Long userId);
}