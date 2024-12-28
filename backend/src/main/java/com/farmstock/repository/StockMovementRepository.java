package com.farmstock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StockMovementRepository extends JpaRepository {
    List findByStockId(Long stockId);
    List findByUserId(Long userId);
}