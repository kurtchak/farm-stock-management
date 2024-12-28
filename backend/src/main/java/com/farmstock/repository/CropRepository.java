package com.farmstock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CropRepository extends JpaRepository {
    List findByHarvestSeason(String harvestSeason);
    List findByNameContainingIgnoreCase(String name);
}