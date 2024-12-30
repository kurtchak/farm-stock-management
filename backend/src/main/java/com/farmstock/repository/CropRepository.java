package com.farmstock.repository;

import com.farmstock.model.Crop;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CropRepository extends JpaRepository<Crop, Long> {
    List<Crop> findByHarvestSeason(String harvestSeason);
    List<Crop> findByNameContainingIgnoreCase(String name);
}