package com.farmstock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface StockRepository extends JpaRepository {
    Optional findByBatchCode(String batchCode);
    List findByCropId(Long cropId);
    List findByQuantityLessThan(Double quantity);
}