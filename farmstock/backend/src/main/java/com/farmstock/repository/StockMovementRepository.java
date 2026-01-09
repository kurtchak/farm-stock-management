package com.farmstock.repository;

import com.farmstock.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByStockId(Long stockId);
    List<StockMovement> findByUserId(Long userId);
}