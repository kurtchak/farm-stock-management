package com.farmstock.repository;

import com.farmstock.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByBatchCode(String batchCode);

    @Query(value = "SELECT COALESCE(MAX(CAST(SUBSTRING(batch_code, -3) AS INTEGER)), 0) " +
            " FROM stocks WHERE batch_code LIKE :pattern", nativeQuery = true)
    Optional<Integer> findMaxSequenceForPattern(@Param("pattern") String pattern);

    @Query("SELECT s FROM Stock s LEFT JOIN FETCH s.crop WHERE s.deleted = false")
    List<Stock> findAllWithCrop();

    @Query("SELECT s FROM Stock s WHERE s.deleted = false")
    List<Stock> findAllActive();

    List<Stock> findByCropId(Long cropId);

    @Query("SELECT s FROM Stock s WHERE s.crop.id = :cropId AND s.deleted = false")
    List<Stock> findActiveByCropId(@Param("cropId") Long cropId);

    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Stock s WHERE s.crop.id = :cropId")
    BigDecimal getTotalQuantityByCropId(@Param("cropId") Long cropId);

    @Query("SELECT s FROM Stock s WHERE s.crop.id = :cropId AND s.quantity > 0 ORDER BY s.harvestDate ASC")
    List<Stock> findAvailableStockByCropIdOrderByHarvestDate(@Param("cropId") Long cropId);
}