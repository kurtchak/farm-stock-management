package com.farmstock.repository;

import com.farmstock.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByBatchCode(String batchCode);
    List<Stock> findByCropId(Long cropId);
    List<Stock> findByQuantityLessThan(Double quantity);
}