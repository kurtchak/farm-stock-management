package com.farmstock.repository;

import com.farmstock.model.GardenItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GardenItemRepository extends JpaRepository<GardenItem, Long> {

    @Query("SELECT g FROM GardenItem g WHERE g.deleted = false ORDER BY g.name")
    List<GardenItem> findAllActive();

    @Query("SELECT g FROM GardenItem g WHERE g.deleted = false AND g.category = :category ORDER BY g.name")
    List<GardenItem> findActiveByCategory(@Param("category") String category);

    @Query("SELECT g FROM GardenItem g WHERE g.deleted = false AND g.quantity <= g.minStock")
    List<GardenItem> findLowStock();

    @Query("SELECT COUNT(g) FROM GardenItem g WHERE g.deleted = false")
    long countActive();

    @Query("SELECT COUNT(g) FROM GardenItem g WHERE g.deleted = false AND g.category = :category")
    long countActiveByCategory(@Param("category") String category);
}
