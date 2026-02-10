package com.farmstock.repository;

import com.farmstock.model.PlantingSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlantingSetRepository extends JpaRepository<PlantingSet, Long> {

    @Query("SELECT DISTINCT s FROM PlantingSet s LEFT JOIN FETCH s.items i LEFT JOIN FETCH i.gardenItem WHERE s.active = true ORDER BY s.name")
    List<PlantingSet> findAllActiveWithItems();

    @Query("SELECT DISTINCT s FROM PlantingSet s LEFT JOIN FETCH s.items i LEFT JOIN FETCH i.gardenItem ORDER BY s.name")
    List<PlantingSet> findAllWithItems();
}
