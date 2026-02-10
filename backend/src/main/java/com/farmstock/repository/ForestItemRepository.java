package com.farmstock.repository;

import com.farmstock.model.ForestItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForestItemRepository extends JpaRepository<ForestItem, Long> {

    @Query("SELECT f FROM ForestItem f WHERE f.deleted = false ORDER BY f.name")
    List<ForestItem> findAllActive();

    @Query("SELECT f FROM ForestItem f WHERE f.deleted = false AND f.category = :category ORDER BY f.name")
    List<ForestItem> findActiveByCategory(@Param("category") String category);

    @Query("SELECT f FROM ForestItem f WHERE f.deleted = false AND f.quantity <= f.minStock")
    List<ForestItem> findLowStock();

    @Query("SELECT COUNT(f) FROM ForestItem f WHERE f.deleted = false")
    long countActive();

    @Query("SELECT COUNT(f) FROM ForestItem f WHERE f.deleted = false AND f.category = :category")
    long countActiveByCategory(@Param("category") String category);
}
