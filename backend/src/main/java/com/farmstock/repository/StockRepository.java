package com.farmstock.repository;

import com.farmstock.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByBatchCode(String batchCode);
    @Query(value = "SELECT COALESCE(MAX(CAST(SUBSTRING(batch_code, -3) AS INTEGER)), 0) " +
            " FROM stocks WHERE batch_code LIKE :pattern", nativeQuery = true)
    Optional<Integer> findMaxSequenceForPattern(@Param("pattern") String pattern);
}