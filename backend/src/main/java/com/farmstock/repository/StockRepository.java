package com.farmstock.repository;

import com.farmstock.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByBatchCode(String batchCode);
    List<Stock> findByCropId(Long cropId);
    List<Stock> findByQuantityLessThan(Double quantity);
}