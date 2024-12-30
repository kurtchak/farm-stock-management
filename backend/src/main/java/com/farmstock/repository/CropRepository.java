package com.farmstock.repository;

import com.farmstock.model.Crop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CropRepository extends JpaRepository<Crop, Long> {
    List<Crop> findByHarvestSeason(String harvestSeason);
    List<Crop> findByNameContainingIgnoreCase(String name);
}